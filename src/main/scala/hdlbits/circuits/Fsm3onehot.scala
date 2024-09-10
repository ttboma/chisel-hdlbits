package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsFsm3onehot.sv
object HdlBitsFsm3onehot extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsm3onehot,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm3onehot
class HdlBitsFsm3onehot extends RawModule {
  val state = IO(Input(UInt(4.W)))
  val next_state = IO(Output(UInt(4.W)))
  val in = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  next_state := Cat(
    (state(2) & in),
    (state(1) & ~in) | (state(3) & ~in),
    (state(0) & in) | (state(1) & in) | (state(3) & in),
    (state(0) & ~in) | (state(2) & ~in)
  )

  out := state(3)
}

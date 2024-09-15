package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file FsmOnehot.sv
object HdlBitsFsmOnehot extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmOnehot,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_onehot
class HdlBitsFsmOnehot extends RawModule {
  val in = IO(Input(Bool()))
  val state = IO(Input(UInt(10.W)))
  val next_state = IO(Output(UInt(10.W)))
  val out1 = IO(Output(Bool()))
  val out2 = IO(Output(Bool()))

  // State transition logic
  val next_state_vec = VecInit(next_state.asBools)
  next_state_vec(0) := ~in & (state(0) | state(1) | state(2) | state(3) | state(
    4
  ) | state(7) | state(8) | state(9))
  next_state_vec(1) := in & (state(0) | state(8) | state(9))
  next_state_vec(2) := in & state(1)
  next_state_vec(3) := in & state(2)
  next_state_vec(4) := in & state(3)
  next_state_vec(5) := in & state(4)
  next_state_vec(6) := in & state(5)
  next_state_vec(7) := in & (state(6) | state(7))
  next_state_vec(8) := ~in & state(5)
  next_state_vec(9) := ~in & state(6)
  next_state := next_state_vec.asUInt

  // Output logic
  out1 := state(8) | state(9)
  out2 := state(9) | state(7)
}

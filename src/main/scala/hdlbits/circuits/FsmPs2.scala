package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file FsmPs2.sv
object HdlBitsFsmPs2 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmPs2,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_ps2
class HdlBitsFsmPs2 extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(Input(UInt(8.W)))
  val done = IO(Output(Bool()))

  // Define state parameters
  val byte1 :: byte2 :: byte3 :: idle :: Nil = Enum(4)
  val nextState = WireInit(
    idle
  ) // NOTE: This is a default value, missing which will cause a compile error

  // State register with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(idle) }

  // State transition logic
  switch(state) {
    is(idle) {
      when(in(3)) {
        nextState := byte1
      }
    }
    is(byte1) {
      nextState := byte2
    }
    is(byte2) {
      nextState := byte3
    }
    is(byte3) {
      when(in(3)) {
        nextState := byte1
      }
    }
  }

  // State update
  state := nextState

  // Output logic
  done := state === byte3
}

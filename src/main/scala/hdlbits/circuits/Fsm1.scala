package hdlbits.verilog_language

import chisel3._
import chisel3.util.{switch, is}

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsFsm1.sv
object HdlBitsFsm1 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsm1,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm1
class HdlBitsFsm1 extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(Input(AsyncReset())) // Asynchronous reset to `true`
  val in =
    IO(Input(Bool()))
  val out =
    IO(Output(Bool()))

  val state = withClockAndReset(clk, areset) { RegInit(true.B) }
  val nextState = WireDefault(state)

  switch(state) {
    is(false.B) {
      nextState := Mux(in, false.B, true.B)
    }
    is(true.B) {
      nextState := Mux(in, true.B, false.B)
    }
  }

  state := nextState
  out := state
}

package hdlbits.circuits

import chisel3._
import chisel3.util.{switch, is}

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsFsm2.sv
object HdlBitsFsm2 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsm2,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm1
class HdlBitsFsm2 extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(Input(AsyncReset())) // Asynchronous reset to OFF
  val j, k =
    IO(Input(Bool()))
  val out =
    IO(Output(Bool()))

  val state = withClockAndReset(clk, areset) { RegInit(false.B) }
  val nextState = WireDefault(state)

  switch(state) {
    is(false.B) {
      nextState := Mux(j, true.B, false.B)
    }
    is(true.B) {
      nextState := Mux(k, false.B, true.B)
    }
  }

  state := nextState
  out := state
}

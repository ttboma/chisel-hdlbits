package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsFsm3s.sv
object HdlBitsFsm3s extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsm3s,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm3s
class HdlBitsFsm3s extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset to `true`
  val in =
    IO(Input(Bool()))
  val out =
    IO(Output(Bool()))

  val A = "b00".U(2.W)
  val B = "b01".U(2.W)
  val C = "b10".U(2.W)
  val D = "b11".U(2.W)

  val state = withClockAndReset(clk, reset) { RegInit(A) }
  val nextState = WireDefault(A)

  // State transition logic
  state := nextState

  // State flip-flops with synchronous reset
  switch(state) {
    is(A) {
      nextState := Mux(in, B, A)
    }
    is(B) {
      nextState := Mux(in, B, C)
    }
    is(C) {
      nextState := Mux(in, D, A)
    }
    is(D) {
      nextState := Mux(in, B, C)
    }
  }

  // Output logic
  out := Mux(state === D, true.B, false.B)
}

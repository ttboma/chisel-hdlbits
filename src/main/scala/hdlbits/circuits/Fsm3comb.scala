package hdlbits.circuits

import chisel3._
import chisel3.util.{switch, is}

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsFsm3comb.sv
object HdlBitsFsm3comb extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsm3comb,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm3comb
class HdlBitsFsm3comb extends RawModule {
  val clk = IO(Input(Bool()))
  val state = IO(Input(UInt(2.W)))
  val next_state = IO(Output(UInt(2.W)))
  val in = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  // Define states
  val A = "b00".U
  val B = "b01".U
  val C = "b10".U
  val D = "b11".U

  // Default output values
  next_state := A
  out := false.B

  // State transition logic
  switch(state) {
    is(A) {
      next_state := Mux(in, B, A)
      out := false.B
    }
    is(B) {
      next_state := Mux(in, B, C)
      out := false.B
    }
    is(C) {
      next_state := Mux(in, D, A)
      out := false.B
    }
    is(D) {
      next_state := Mux(in, B, C)
      out := true.B
    }
  }
}

package hdlbits.circuits

import chisel3._
import chisel3.util.{switch, is}

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsExamsEce2412014Q5a.sv
object HdlBitsExamsEce2412014Q5a extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExamsEce2412014Q5a,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2014_q5a
class HdlBitsExamsEce2412014Q5a extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(Input(Bool()))
  val x = IO(Input(Bool()))
  val z = IO(Output(Bool()))

  // State register with clock and reset
  val state = withClockAndReset(clk, areset.asAsyncReset) {
    RegInit(0.U(2.W))
  }

  // State transition logic
  switch(state) {
    is(0.U) {
      state := Mux(x, 1.U, 0.U)
    }
    is(1.U) {
      state := Mux(x, 2.U, 1.U)
    }
    is(2.U) {
      state := Mux(x, 2.U, 1.U)
    }
  }
  // Output logic
  z := state === 1.U
}

package hdlbits.circuits

import chisel3._
import chisel3.util.{switch, is}

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsExamsEce2412014Q5b.sv
object HdlBitsExamsEce2412014Q5b extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExamsEce2412014Q5b,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2014_q5b
class HdlBitsExamsEce2412014Q5b extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(Input(Bool()))
  val x = IO(Input(Bool()))
  val z = IO(Output(Bool()))

  // State register with clock and reset
  val state = withClockAndReset(clk, areset.asAsyncReset) {
    RegInit(0.U(1.W))
  }

  // State transition logic
  switch(state) {
    is(0.U) {
      state := Mux(x, 1.U, 0.U)
    }
  }
  // Output logic
  z := (state === 0.U && x) || (state === 1.U && !x)
}

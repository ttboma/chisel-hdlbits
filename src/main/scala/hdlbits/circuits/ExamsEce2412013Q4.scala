package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file ExamsEce2412013Q4.sv
object ExamsEce2412013Q4 extends App {
  ChiselStage.emitSystemVerilogFile(
    new ExamsEce2412013Q4,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q4
class ExamsEce2412013Q4 extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset to `true`
  val s = IO(Input(UInt(3.W)))
  val fr3 = IO(Output(Bool()))
  val fr2 = IO(Output(Bool()))
  val fr1 = IO(Output(Bool()))
  val dfr = IO(Output(Bool()))

  // Define states as constants
  val A = "b111".U(3.W)
  val B = "b011".U(3.W)
  val C = "b001".U(3.W)
  val D = "b000".U(3.W)

  // State registers with clock and reset
  val sState = withClockAndReset(clk, reset) { RegInit(D) }
  val dfrState = withClockAndReset(clk, reset) { RegInit(true.B) }

  // State transition logic
  sState := s
  when(s < sState) {
    dfrState := true.B
  }.elsewhen(s > sState) {
    dfrState := false.B
  }

  // Output logic for fr1, fr2, fr3, and dfr
  fr1 := !sState(2)
  fr2 := !sState(1)
  fr3 := !sState(0)
  dfr := dfrState
}

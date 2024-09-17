package hdlbits.building_larger_circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file ExamsReview2015Count1k.sv
object HdlBitsExamsReview2015Count1k extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExamsReview2015Count1k,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/building_larger_circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/review2015_count1k
class HdlBitsExamsReview2015Count1k extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val q = IO(Output(UInt(10.W)))

  val counter = withClockAndReset(clk, reset) { RegInit(0.U(10.W)) }

  when(counter === 999.U) {
    counter := 0.U
  }.otherwise {
    counter := counter + 1.U
  }

  q := counter
}

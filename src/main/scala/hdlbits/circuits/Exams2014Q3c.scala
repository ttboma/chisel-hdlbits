package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Exams2014Q3c.sv
object HdlBitsExams2014Q3c extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExams2014Q3c,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/2014_q3c
class HdlBitsExams2014Q3c extends RawModule {
  val clk = IO(Input(Clock()))
  val y = IO(Input(UInt(3.W)))
  val x = IO(Input(Bool()))
  val Y0 = IO(Output(Bool()))
  val z = IO(Output(Bool()))

  // Output logic
  Y0 := false.B
  z := false.B
  switch(y) {
    is(0.U) {
      Y0 := x
      z := false.B
    }
    is(1.U) {
      Y0 := ~x
      z := false.B
    }
    is(2.U) {
      Y0 := x
      z := false.B
    }
    is(3.U) {
      Y0 := ~x
      z := true.B
    }
    is(4.U) {
      Y0 := ~x
      z := true.B
    }
  }
}

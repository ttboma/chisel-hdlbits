package hdlbits.circuits

import chisel3._
import chisel3.util.{switch, is}

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file HdlBitsExamsEce2412013Q8.sv
object HdlBitsExamsEce2412013Q8 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExamsEce2412013Q8,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q8
class HdlBitsExamsEce2412013Q8 extends RawModule {
  val clk = IO(Input(Clock()))
  val aresetn = IO(Input(Bool()))
  val x = IO(Input(Bool()))
  val z = IO(Output(Bool()))

  // State register with clock and reset
  val state = withClockAndReset(clk, (!aresetn).asAsyncReset) {
    RegInit(0.U(3.W))
  }

  // State transition logic
  switch(state) {
    is(0.U) {
      when(x) {
        state := 1.U
      }
    }
    is(1.U) {
      when(!x) {
        state := 2.U
      }
    }
    is(2.U) {
      when(x) {
        state := 1.U
      }.otherwise {
        state := 0.U
      }
    }
  }
  // Output logic
  z := state === 2.U && x
}

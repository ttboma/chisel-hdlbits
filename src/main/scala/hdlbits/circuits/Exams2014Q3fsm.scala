package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Exams2014Q3fsm.sv
object HdlBitsExams2014Q3fsm extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExams2014Q3fsm,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/2014_q3_fsm
class HdlBitsExams2014Q3fsm extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val s = IO(Input(Bool()))
  val w = IO(Input(Bool()))
  val z = IO(Output(Bool()))

  // Define states as constants
  val a :: b0 :: b1 :: b2 :: Nil = Enum(4)

  // State registers with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(a) }
  val counter = withClockAndReset(clk, reset) { RegInit(0.U(2.W)) }

  // State transition logic
  switch(state) {
    is(a) {
      when(s) {
        state := b0
      }
    }
    is(b0) {
      state := b1
      when(w) {
        counter := 1.U
      }.otherwise {
        counter := 0.U
      }
    }
    is(b1) {
      state := b2
      when(w) {
        counter := counter + 1.U
      }
    }
    is(b2) {
      state := b0
      when(w) {
        counter := counter + 1.U
      }
    }
  }

  // Output logic
  z := state === b0 && counter === 2.U
}

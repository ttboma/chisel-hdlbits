package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Exams2014Q3bfsm.sv
object HdlBitsExams2014Q3bfsm extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExams2014Q3bfsm,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/2014_q3bfsm
class HdlBitsExams2014Q3bfsm extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val x = IO(Input(Bool()))
  val z = IO(Output(Bool()))

  // State registers with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(0.U(3.W)) }

  // State transition logic
  switch(state) {
    is(0.U) {
      state := Mux(x, 1.U, 0.U)
    }
    is(1.U) {
      state := Mux(x, 4.U, 1.U)
    }
    is(2.U) {
      state := Mux(x, 1.U, 2.U)
    }
    is(3.U) {
      state := Mux(x, 2.U, 1.U)
    }
    is(4.U) {
      state := Mux(x, 4.U, 3.U)
    }
  }

  // Output logic
  z := state === 4.U || state === 3.U
}

package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Exams2013Q2bfsm.sv
object HdlBitsExams2013Q2bfsm extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsExams2013Q2bfsm,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Exams/2013_q2bfsm
class HdlBitsExams2013Q2bfsm extends RawModule {
  val clk = IO(Input(Clock()))
  val resetn = IO(Input(Bool())) // active-low synchronous reset
  val x = IO(Input(Bool()))
  val y = IO(Input(Bool()))
  val f = IO(Output(Bool()))
  val g = IO(Output(Bool()))

  // Define state parameters
  val a :: b :: c :: d :: e :: f_ :: g_ :: h :: i :: Nil = Enum(9)

  // State registers with clock and reset
  val state = withClockAndReset(clk, !resetn) { RegInit(a) }

  // State transition logic
  switch(state) {
    is(a) {
      state := b
    }
    is(b) {
      state := c
    }
    is(c) {
      when(x) {
        state := d
      }
    }
    is(d) {
      when(!x) {
        state := e
      }
    }
    is(e) {
      when(!x) {
        state := c
      } otherwise (
        state := f_
      )
    }
    is(f_) {
      when(!y) {
        state := g_
      } otherwise {
        state := h
      }
    }
    is(g_) {
      when(!y) {
        state := i
      } otherwise {
        state := h
      }
    }
  }

  // Output logic
  f := state === b
  g := state === f_ || state === g_ || state === h
}

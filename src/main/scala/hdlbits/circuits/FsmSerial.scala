package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

object HdlBitsFsmSerial extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmSerial,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_serial
class HdlBitsFsmSerial extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(Input(Bool()))
  val done = IO(Output(Bool()))

  // Define state parameters
  val idle :: start :: stop :: Nil = Enum(3)

  // State register with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(idle) }
  val counter = withClockAndReset(clk, reset) { RegInit(0.U(3.W)) }

  // State transition logic
  switch(state) {
    is(idle) {
      when(!in) {
        state := start
      }
      counter := 0.U
    }
    is(start) {
      when(counter === 7.U) {
        state := stop
      } otherwise {
        counter := counter + 1.U
      }
    }
    is(stop) {
      when(in) {
        state := idle
      } otherwise {
        counter := 0.U
      }
    }
  }

  // State update
  done := state === idle && counter === 7.U
}

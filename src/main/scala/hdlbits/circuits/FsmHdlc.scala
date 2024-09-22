package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file FsmPs2.sv
object HdlBitsFsmHdlc extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmHdlc,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_hdlc
class HdlBitsFsmHdlc extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(
    Input(Bool())
  )
  val disc = IO(Output(Bool()))
  val flag = IO(Output(Bool()))
  val err = IO(Output(Bool()))

  // Define state parameters
  val dataS :: discS :: flagS :: errS :: Nil = Enum(4)

  // State register with clock and reset

  val state = withClockAndReset(clk, reset) { RegInit(dataS) }
  val counter = withClockAndReset(clk, reset) {
    RegInit(0.U(3.W))
  }

  // State transition logic
  counter := Mux(in, counter + 1.U, 0.U)
  switch(state) {
    is(dataS) {
      when(counter === 5.U) {
        state := Mux(in, state, discS)
      }.elsewhen(counter === 6.U) {
        state := Mux(in, errS, flagS)
      }
    }
    is(discS) {
      state := dataS
    }
    is(flagS) {
      state := dataS
    }
    is(errS) {
      state := Mux(in, state, dataS)
    }
  }

  // Output logic
  disc := state === discS
  flag := state === flagS
  err := state === errS
}

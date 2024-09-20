package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguated from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

object VerilogHdlBitsFsmSerialdp extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmSerialdp,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_serialdp
class HdlBitsFsmSerialdp extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(Input(Bool()))
  val out_byte = IO(Output(UInt(8.W)))
  val done = IO(Output(Bool()))

  // Define state parameters
  val idle :: start :: parity :: stop :: Nil = Enum(4)

  // State register with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(idle) }
  val counter = withClockAndReset(clk, reset) { RegInit(0.U(3.W)) }
  val outByte = withClockAndReset(clk, reset) {
    RegInit(VecInit(Seq.fill(8)(false.B)))
  }
  val parityState = Module(new parity)
  parityState.clk := clk
  parityState.reset := reset | (state === idle && !in)
  parityState.in := in

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
        state := parity // NOTE: `parity` state not conflict with `parity` module, which is different from SpinalHDL
      } otherwise {
        counter := counter + 1.U
      }
      outByte(counter) := in
    }
    is(parity) {
      state := stop
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
  // The last state `stop` needs `in` to be high, which have to included in
  // the parity check, so odd need to be initialized to true
  done := state === idle && counter === 7.U && ~parityState.odd
  out_byte := outByte.asUInt
}

// FIX: I would like to use `Parity` as class name and `parity as generated module name. But How?
class parity extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(Input(Bool()))
  val odd = IO(Output(Bool()))

  val oddState = withClockAndReset(clk, reset) { RegInit(false.B) }

  when(in) {
    oddState := ~oddState
  }

  odd := oddState
}

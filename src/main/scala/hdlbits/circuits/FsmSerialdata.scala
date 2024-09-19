package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

object VerilogHdlBitsFsmSerialdata extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmSerialdata,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_serialdata
class HdlBitsFsmSerialdata extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(Input(Bool()))
  val out_byte = IO(Output(UInt(8.W)))
  val done = IO(Output(Bool()))

  // Define state parameters
  val idle :: start :: stop :: Nil = Enum(3)

  // State register with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(idle) }
  val counter = withClockAndReset(clk, reset) { RegInit(0.U(3.W)) }
  val outByte = withClockAndReset(clk, reset) {
    RegInit(VecInit(Seq.fill(8)(false.B)))
  }

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
      outByte(counter) := in
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
  out_byte := outByte.asUInt
}

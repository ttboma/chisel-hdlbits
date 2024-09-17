package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file FsmPs2data.sv
object HdlBitsFsmPs2data extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsFsmPs2data,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Fsm_ps2data
class HdlBitsFsmPs2data extends RawModule {
  val clk = IO(Input(Clock()))
  val reset = IO(Input(Bool())) // Synchronous reset
  val in = IO(Input(UInt(8.W)))
  val out_bytes = IO(Output(UInt(24.W)))
  val done = IO(Output(Bool()))

  // Define state parameters
  val idle :: byte1 :: byte2 :: byte3 :: Nil = Enum(4)
  val nextState = WireInit(
    idle
  ) // NOTE: This is a default value, missing which will cause a compile error

  // State register with clock and reset
  val state = withClockAndReset(clk, reset) { RegInit(idle) }
  val dataPath = withClockAndReset(clk, reset) { RegInit(0.U(24.W)) }

  // State transition logic
  switch(state) {
    is(idle) {
      when(in(3)) {
        nextState := byte1
      }
    }
    is(byte1) {
      nextState := byte2
    }
    is(byte2) {
      nextState := byte3
    }
    is(byte3) {
      when(in(3)) {
        nextState := byte1
      }
    }
  }

  // State update
  state := nextState
  dataPath := (dataPath << 8) | in // NOTE: different bit width is NOT warned by Chisel

  // Output logic
  out_bytes := dataPath
  done := state === byte3
}

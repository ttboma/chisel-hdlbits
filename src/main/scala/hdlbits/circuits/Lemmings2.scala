package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Lemmings2.sv
object HdlBitsLemmings2 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsLemmings2,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Lemmings2
class HdlBitsLemmings2 extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(
    Input(AsyncReset())
  ) // Freshly brainwashed Lemmings walk left.
  val bump_left = IO(Input(Bool()))
  val bump_right = IO(Input(Bool()))
  val ground = IO(Input(Bool()))
  val walk_left = IO(Output(Bool()))
  val walk_right = IO(Output(Bool()))
  val aaah = IO(Output(Bool()))

  // State registers with clock and reset
  val walkDirection = withClockAndReset(clk, areset) { RegInit(false.B) }
  val groundState = withClockAndReset(clk, areset) { RegInit(true.B) }

  // State transition logic
  groundState := ground

  when(!ground || !groundState) {}
    .elsewhen(bump_left & bump_right) {
      walkDirection := ~walkDirection
    }
    .elsewhen(bump_right) {
      walkDirection := false.B
    }
    .elsewhen(bump_left) {
      walkDirection := true.B
    }

  // Output logic
  walk_left := Mux(groundState, ~walkDirection, false.B)
  walk_right := Mux(groundState, walkDirection, false.B)
  aaah := ~groundState
}

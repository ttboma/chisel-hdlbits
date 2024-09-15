package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Lemmings1.sv
object HdlBitsLemmings1 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsLemmings1,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Lemmings1
class HdlBitsLemmings1 extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(
    Input(AsyncReset())
  ) // Freshly brainwashed Lemmings walk left.
  val bump_left = IO(Input(Bool()))
  val bump_right = IO(Input(Bool()))
  val walk_left = IO(Output(Bool()))
  val walk_right = IO(Output(Bool()))

  val walkDirection = withClockAndReset(clk, areset) { RegInit(false.B) }

  when(bump_left & bump_right) {
    walkDirection := ~walkDirection
  }.elsewhen(bump_right) {
    walkDirection := false.B
  }.elsewhen(bump_left) {
    walkDirection := true.B
  }

  walk_left := ~walkDirection
  walk_right := walkDirection
}

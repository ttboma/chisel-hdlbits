package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Lemmings3.sv
object HdlBitsLemmings3 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsLemmings3,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Lemmings3
class HdlBitsLemmings3 extends RawModule {
  val clk = IO(Input(Clock()))
  val areset = IO(
    Input(AsyncReset())
  ) // Freshly brainwashed Lemmings walk left.
  val bump_left = IO(Input(Bool()))
  val bump_right = IO(Input(Bool()))
  val ground = IO(Input(Bool()))
  val dig = IO(Input(Bool()))
  val walk_left = IO(Output(Bool()))
  val walk_right = IO(Output(Bool()))
  val aaah = IO(Output(Bool()))
  val digging = IO(Output(Bool()))

  // Define states
  val walkingLeft :: walkingRight :: fallingLeft :: fallingRight :: diggingLeft :: diggingRight :: Nil =
    Enum(6)

  // State register with clock and reset
  val state =
    withClockAndReset(clk, areset) { RegInit(walkingLeft) }

  // State transition logic
  switch(state) {
    is(walkingLeft) {
      when(!ground) {
        state := fallingLeft
      }.elsewhen(dig) {
        state := diggingLeft
      }.elsewhen(bump_left) {
        state := walkingRight
      }
    }
    is(walkingRight) {
      when(!ground) {
        state := fallingRight
      }.elsewhen(dig) {
        state := diggingRight
      }.elsewhen(bump_right) {
        state := walkingLeft
      }
    }
    is(fallingLeft) {
      when(ground) {
        state := walkingLeft
      }
    }
    is(fallingRight) {
      when(ground) {
        state := walkingRight
      }
    }
    is(diggingLeft) {
      when(!ground) {
        state := fallingLeft
      }
    }
    is(diggingRight) {
      when(!ground) {
        state := fallingRight
      }
    }
  }

  // Output logic
  walk_left := state === walkingLeft
  walk_right := state === walkingRight
  aaah := state === fallingLeft || state === fallingRight
  digging := state === diggingLeft || state === diggingRight
}

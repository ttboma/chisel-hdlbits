package hdlbits.circuits

import chisel3._
import chisel3.util._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// Generate Verilog sources and save it in file Lemmings4.sv
object HdlBitsLemmings4 extends App {
  ChiselStage.emitSystemVerilogFile(
    new HdlBitsLemmings4,
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info"
    ),
    args = Array("--target-dir", "gen/hdlbits/circuits")
  )
}

// https://hdlbits.01xz.net/wiki/Lemmings4
class HdlBitsLemmings4 extends RawModule {
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

  // Define state parameters
  val walkingLeft :: walkingRight :: fallingLeft :: fallingRight :: diggingLeft :: diggingRight :: splat :: dead :: Nil =
    Enum(8)
  val nextState = WireInit(walkingLeft)

  // State register with clock and reset
  val state =
    withClockAndReset(clk, areset) { RegInit(walkingLeft) }
  val counter =
    withClockAndReset(clk, areset) { RegInit(0.U(5.W)) }

  // State transition logic
  switch(state) {
    is(walkingLeft) {
      when(!ground) {
        nextState := fallingLeft
      }.elsewhen(dig) {
        nextState := diggingLeft
      }.elsewhen(bump_left) {
        nextState := walkingRight
      }.otherwise {
        nextState := walkingLeft
      }
    }
    is(walkingRight) {
      when(!ground) {
        nextState := fallingRight
      }.elsewhen(dig) {
        nextState := diggingRight
      }.elsewhen(bump_right) {
        nextState := walkingLeft
      }.otherwise {
        nextState := walkingRight
      }
    }
    is(fallingLeft) {
      when(!ground & counter <= 20.U) {
        nextState := fallingLeft
      }.elsewhen(!ground & counter > 20.U) {
        nextState := splat
      }.otherwise {
        nextState := walkingLeft
      }
    }
    is(fallingRight) {
      when(!ground & counter <= 20.U) {
        nextState := fallingRight
      }.elsewhen(!ground & counter > 20.U) {
        nextState := splat
      }.otherwise {
        nextState := walkingRight
      }
    }
    is(diggingLeft) {
      when(!ground) {
        nextState := fallingLeft
      } otherwise {
        nextState := diggingLeft
      }
    }
    is(diggingRight) {
      when(!ground) {
        nextState := fallingRight
      } otherwise {
        nextState := diggingRight
      }
    }
    is(splat) {
      when(ground) {
        nextState := dead
      } otherwise {
        nextState := splat
      }
    }
    is(dead) {
      nextState := dead
    }
  }

  // State flip-flops with asynchronous reset
  state := nextState
  when(nextState === fallingLeft | nextState === fallingRight) {
    counter := counter + 1.U
  } otherwise {
    counter := 1.U
  }

  // Output logic
  walk_left := state === walkingLeft
  walk_right := state === walkingRight
  aaah := state === fallingLeft | state === fallingRight | state === splat
  digging := state === diggingLeft | state === diggingRight
}

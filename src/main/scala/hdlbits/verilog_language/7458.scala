package hdlbits.verilog_language

import chisel3._

// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

// https://hdlbits.01xz.net/wiki/7458
case class HdlBits7458() extends Module {
  val p1a = IO(Input(Bool()))
  val p1b = IO(Input(Bool()))
  val p1c = IO(Input(Bool()))
  val p1d = IO(Input(Bool()))
  val p1e = IO(Input(Bool()))
  val p1f = IO(Input(Bool()))
  val p1y = IO(Output(Bool()))
  val p2a = IO(Input(Bool()))
  val p2b = IO(Input(Bool()))
  val p2c = IO(Input(Bool()))
  val p2d = IO(Input(Bool()))
  val p2y = IO(Output(Bool()))

  p1y := (p1a & p1c & p1b) | (p1f & p1e & p1d)
  p2y := (p2a & p2b) | (p2c & p2d)
}

// Generate Verilog sources and save it in file HdlBits7458.sv
object HdlBits7458 extends App {
  ChiselStage.emitSystemVerilogFile(
    HdlBits7458(),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info"),
    args = Array("--target-dir", "gen/hdlbits/verilog_language")
  )
}

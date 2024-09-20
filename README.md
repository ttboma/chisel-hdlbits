# Introduction

This is a demo project using [Chisel](https://github.com/chipsalliance/chisel). It begins with the [Chisel template](https://github.com/chipsalliance/chisel-template) and includes additional **pre-commit** configuration.

I have included several practice solutions in the `src/main/scala/hdlbits` folder. The generated simulation files and Verilog files will be placed in the `gen` and `simWorkspace` folders.

Please note that these solutions are not necessarily optimal, but rather intended to help you gradually work through the SpinalHDL language.

## Comparison to SpinalHDL

* In Chisel, individual bit assignment doesn’t work with `UInt` and `Bits`. See <src/main/scala/hdlbits/circuits/Fsm3onehot.scala>
* In Chisel, cannot set the name of the generated top module name. See <src/main/scala/hdlbits/verilog_language/7458.scala>
* In Chisel, the generated Verilog file is optimized, making it difficult to inspect directly when you're still learning and the generated DUT isn't correct. In contrast, with SpinalHDL, you can more easily review the generated Verilog to understand its equivalent and identify issues.
* In Chisel, it is more verbose to do bit assignment. For comparison, see `\src\main\scala\hdlbits\circuits\FsmSerialdata.scala:34`.
* In Chisel, state name does not conflict with module name. See `src\main\scala\hdlbits\circuits\FsmSerialdp.scala:39` for comparison.
* In Chisel, we can control submodule reset signal less verbosely than SpinalHDL. See `src\main\scala\hdlbits\circuits\FsmSerialdp.scala:52` for example. And SpinalHDL is not.

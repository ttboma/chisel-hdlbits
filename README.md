# Introduction

This is a demo project using [Chisel](https://github.com/chipsalliance/chisel). It begins with the [Chisel template](https://github.com/chipsalliance/chisel-template) and includes additional **pre-commit** configuration.

I have included several practice solutions in the `src/main/scala/hdlbits` folder. The generated simulation files and Verilog files will be placed in the `gen` and `simWorkspace` folders.

Please note that these solutions are not necessarily optimal, but rather intended to help you gradually work through the SpinalHDL language.

## Comparison to SpinalHDL

* In Chisel, individual bit assignment doesn’t work with `UInt` and `Bits`. See <src/main/scala/hdlbits/circuits/Fsm3onehot.scala>
* In Chisel, cannot set the name of the generated top module name. See <src/main/scala/hdlbits/verilog_language/7458.scala>

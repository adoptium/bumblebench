
def generate_body(file:str, i: int) -> None:
    with open(file, 'a') as f:
        f.write("public static int temp_function"+str(i)+"(){ \n")
        f.write("int a = 3 + 4;\n")
        f.write("int b = (int) (a + Math.random() * 10);\n")
        if (i > 0):
            f.write("return b + temp_function"+str(i-1)+"();\n")
        else:
            f.write("return b;\n")
        f.write("}\n")
    if (i> 0):    
        generate_body(file,i-1)
    else:
        return

if __name__ == '__main__':
    i = 10
    file = "../net/adoptopenjdk/bumblebench/examples/generated_code"+str(i)+".java"

    with open(file, 'w') as f:
        f.write("package net.adoptopenjdk.bumblebench.examples; \n \n")
        f.write("import net.adoptopenjdk.bumblebench.core.MicroBench; \n\n")
        f.write("public final class generated_code"+str(i)+" extends MicroBench { \n\n")
        f.write("protected long doBatch(long numIterations) throws InterruptedException { \n\n")
        f.write("int z = temp_function"+str(i)+"(); \n")
        f.write("System.out.println(z); \n")
        f.write("return numIterations; \n")
        f.write("} \n")
    generate_body(file, i)
    with open(file, 'a') as f:
        f.write("\n}")



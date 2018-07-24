/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.*;

import java.io.FileOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;




public class ComplexIndyGenerator {

	public static void main(String[] args) throws Throwable {
		FileOutputStream fos = new FileOutputStream("ComplexIndy.class");
		fos.write(makeExample());
		fos.flush();
		fos.close();
	}
	
	   public static byte[] makeExample() throws Throwable {
		      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		      cw.visit(V1_7, ACC_PUBLIC | ACC_SUPER, "ComplexIndy", null, "java/lang/Object", null);
		      		      
		      MethodVisitor mv;
		      {
		         mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "gwtTest", "(Ljava/lang/Object;)Ljava/lang/String;", null, null);
		         mv.visitCode();
			 mv.visitVarInsn(ALOAD, 0);
		         mv.visitInvokeDynamicInsn("gwtBootstrap", "(Ljava/lang/Object;)Ljava/lang/String;",
		               new Handle(
		            		   H_INVOKESTATIC, 
		            		   "BootstrapMethods", 
		            		   "fibBootstrap",
		            		   Type.getType(
		            				 "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
		            		   ).getDescriptor())
		         );
		         mv.visitInsn(ARETURN);
		         mv.visitMaxs(0, 0);
		         mv.visitEnd();
		      }

		{
			mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "fibIndy", "(I)I", null, null);
			mv.visitCode();
			mv.visitVarInsn(ILOAD, 0);
			mv.visitInvokeDynamicInsn("fibBootstrap", "(I)I",
				new Handle(
					H_INVOKESTATIC, 
					"BootstrapMethods", 
					"fibBootstrap",
					Type.getType(
						"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
					).getDescriptor())
				);
			mv.visitInsn(IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	cw.visitEnd();
	return cw.toByteArray();
	}

}

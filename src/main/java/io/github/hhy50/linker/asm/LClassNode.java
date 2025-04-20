package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.annotations.Field;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public interface LClassNode {

    @Field.Getter("access")
    int getAccess();

    @Field.Getter("name")
    String getName();

    @Field.Getter("superName")
    String getSuperName();

    @Field.Getter("interfaces")
    List<String> getInterfaces();

    @Field.Getter("signature")
    String getSignature();

    @Field.Getter("fields")
    List<Object> getFields();

    @Field.Getter("methods")
    List<LMethodNode> getMethods();

    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value);

    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions);

    AnnotationVisitor visitAnnotation(String descriptor, boolean visible);

    void accept(ClassVisitor classVisitor);
}

package io.github.hhy50.linker.test.nest.case3;


import io.github.hhy50.linker.annotations.Field;


public interface FirstVisitor {


    @Field.Getter("second.three.CLASS_NAME")
    String getThreeClass();

    @Field.Setter("second.three.CLASS_NAME")
    void setThreeClass(String val);
}

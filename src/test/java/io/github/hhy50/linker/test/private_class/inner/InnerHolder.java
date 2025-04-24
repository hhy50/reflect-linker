package io.github.hhy50.linker.test.private_class.inner;

public class InnerHolder {
    Inner inner = new Inner();

    public Inner getInner() {
        return inner;
    }

    Inner run(Inner inner) {
        return inner;
    }
}

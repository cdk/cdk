package org.openscience.cdk.isomorphism.matchers.smarts;

public class LogicalOperator {
    private Object left;

    private String name;

    private Object right;

    public Object getLeft() {
        return left;
    }

    public String getName() {
        return name;
    }

    public Object getRight() {
        return right;
    }

    public void setLeft(Object left) {
        this.left = left;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRight(Object right) {
        this.right = right;
    }
}

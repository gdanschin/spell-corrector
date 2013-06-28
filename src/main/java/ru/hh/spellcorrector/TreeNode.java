package ru.hh.spellcorrector;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;

public class TreeNode<T> {

  private final T value;
  private final List<? extends TreeNode<T>> children;

  public TreeNode(T value) {
    this(value, Collections.<TreeNode<T>>emptyList());
  }

  public TreeNode(T value, TreeNode<T>... children) {
    this(value, Arrays.asList(children));
  }

  public TreeNode(T value, List<? extends TreeNode<T>> children) {
    this.value = value;
    this.children = children;
  }

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public T getValue() {
    return value;
  }

  public Iterable<? extends TreeNode<T>> getChildren() {
    return children;
  }

  public static <T> TreeNode<T> node(T value, T... children) {
    return node(value, asList(children));
  }

  public static <T> TreeNode<T> node(T value, Iterable<T> children) {
    return node(value, ImmutableList.copyOf(
        transform(children, new
            Function<T, TreeNode<T>>() {
              @Override
              public TreeNode<T> apply(T input) {
                return leaf(input);
              }
            }
        )));
  }

  public static <T> TreeNode<T> node(T value, TreeNode<T>... children) {
    return node(value, asList(children));
  }

  public static <T> TreeNode<T> node(T value, List<? extends TreeNode<T>> children) {
    return new TreeNode<T>(value, children);
  }

  public static <T> TreeNode<T> leaf(T value) {
    return new TreeNode<T>(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TreeNode treeNode = (TreeNode) o;

    return Objects.equal(value, treeNode.value) && Objects.equal(children, treeNode.children);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value, children);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("(").append(value);
    if (hasChildren()) {
      builder.append(" -> ").append(children);
    }
    return builder.append(")").toString();
  }
}

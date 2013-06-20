package ru.hh.spellcorrector;

import com.google.common.base.Preconditions;

public class Partition {

  private final String left;
  private final String right;

  public Partition(String left, String right) {
    this.left = Preconditions.checkNotNull(left);
    this.right = Preconditions.checkNotNull(right);
  }

  public static Partition of(String left, String right) {
    return new Partition(left, right);
  }

  public String left() {
    return left;
  }

  public String right() {
    return right;
  }

  @Override
  public int hashCode() {
    return 31 * left.hashCode() + right.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || o.getClass() != getClass()) {
      return false;
    }

    Partition partition = (Partition) o;
    return left.equals(partition.left) && right.equals(partition.right);
  }

  @Override
  public String toString() {
    return "\"" + left +  "|" + right + "\"";
  }
}

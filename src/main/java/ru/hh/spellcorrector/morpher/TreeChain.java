package ru.hh.spellcorrector.morpher;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import ru.hh.spellcorrector.Correction;
import ru.hh.spellcorrector.Itertools;
import ru.hh.spellcorrector.TreeNode;
import java.util.Iterator;
import java.util.Queue;

import static java.util.Collections.singleton;

class TreeChain extends Morpher {

  private final TreeNode<? extends Morpher> chainRoot;

  TreeChain(TreeNode<? extends Morpher> chainRoot) {
    this.chainRoot = chainRoot;
  }

  @Override
  public Iterable<Correction> corrections(final Correction source) {
    return new Iterable<Correction>() {
      @Override
      public Iterator<Correction> iterator() {
        return new TreeChainIterator(source);
      }
    };
  }

  class TreeChainIterator extends AbstractIterator<Correction> {

    private final Queue<ProcessingUnit> queue = Lists.newLinkedList();
    private Iterator<Correction> current;

    public TreeChainIterator(Correction source) {
      queue.add(new ProcessingUnit(chainRoot, singleton(source)));
      current = Iterators.emptyIterator();
    }


    @Override
    protected Correction computeNext() {
      if (current.hasNext()) {
        return current.next();
      }
      return nextUnit();
    }

    private Correction nextUnit() {
      ProcessingUnit unit = queue.poll();
      if (unit == null) {
        return endOfData();
      }
      Iterable<Correction> result = unit.node.getValue().corrections(unit.input);
      if (unit.node.hasChildren()) {
        result = Itertools.memorizeIterable(result);
        for (TreeNode<? extends Morpher> child : unit.node.getChildren()) {
          queue.add(new ProcessingUnit(child, result));
        }
      }
      current = result.iterator();
      return computeNext();
    }
  }

  private static class ProcessingUnit {
    final TreeNode<? extends Morpher> node;
    final Iterable<Correction> input;

    public ProcessingUnit(TreeNode<? extends Morpher> morpher, Iterable<Correction> input) {
      this.node = morpher;
      this.input = input;
    }
  }
}

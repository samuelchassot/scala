/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2006, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$


package scala;

import scala.runtime.compat.StringBuilder
import Predef._;
import scala.collection.mutable.ListBuffer;

/** This object provides methods for creating specialized lists, and for
 *  transforming special kinds of lists (e.g. lists of lists).
 *
 *  @author  Martin Odersky and others
 *  @version 1.0, 15/07/2003
 */
object List {

  /** Create a list with given elements.
   *
   *  @param xs the elements to put in the list
   *  @return the list containing elements xs.
   */
  def apply[A](xs: A*): List[A] = xs.toList;

  /** Create a sorted list of all integers in a range.
   *
   *  @param from the start value of the list
   *  @param end the end value of the list
   *  @return the sorted list of all integers in range [from;end).
   */
  def range(from: Int, end: Int): List[Int] =
    range(from, end, 1);

  /** Create a sorted list of all integers in a range.
   *
   *  @param from the start value of the list
   *  @param end the end value of the list
   *  @param step the increment value of the list
   *  @return the sorted list of all integers in range [from;end).
   */
  def range(from: Int, end: Int, step: Int): List[Int] = {
    val b = new ListBuffer[Int]
    var i = from
    while (i < end) {
      b += i
      i = i + step
    }
    b.toList
  }

  /** Create a sorted list of all integers in a range.
   *
   *  @param from the start value of the list
   *  @param end the end value of the list
   *  @param step the increment function of the list
   *  @return the sorted list of all integers in range [from;end).
   */
  def range(from: Int, end: Int, step: Int => Int): List[Int] = {
    val b = new ListBuffer[Int]
    var i = from
    while (i < end) {
      b += i
      i = i + step(i)
    }
    b.toList
  }

  /** Create a list containing several copies of an element.
   *
   *  @param n the length of the resulting list
   *  @param elem the element composing the resulting list
   *  @return a list composed of n elements all equal to elem
   */
  def make[a](n: Int, elem: a): List[a] = {
    val b = new ListBuffer[a]
    var i = 0
    while (i < n) {
      b += elem
      i = i + 1
    }
    b.toList
  }

  /** Create a list by applying a function to successive integers.
   *
   *  @param n     the length of the resulting list
   *  @param maker the procedure which, given an integer n, returns the
   *               nth element of the resulting list, where n is in [0;n).
   *  @return the list obtained by applying the maker function to successive
   *          integers from 0 to n (exclusive).
   */
  def tabulate[a](n: Int, maker: Int => a): List[a] = {
    val b = new ListBuffer[a]
    var i = 0
    while (i < n) {
      b += maker(i)
      i = i + 1
    }
    b.toList
  }

  /** Concatenate all the elements of a given list of lists.
   *  @param xss the list of lists that are to be concatenated
   *  @return the concatenation of all the lists
   */
  def flatten[a](xss: List[List[a]]): List[a] = concat(xss: _*)

  /** Concatenate all the argument lists into a single list.
   *  @param xss the lists that are to be concatenated
   *  @return the concatenation of all the lists
   */
  def concat[a](xss: List[a]*): List[a] = {
    val b = new ListBuffer[a]
    for (val xs <- xss) {
      var xc = xs
      while (!xc.isEmpty) {
        b += xc.head
        xc = xc.tail
      }
    }
    b.toList
  }

  /** Transforms a list of pair into a pair of lists.
   *
   *  @param xs the list of pairs to unzip
   *  @return a pair of lists: the first list in the pair contains the list
   */
  def unzip[a,b](xs: List[Pair[a,b]]): Pair[List[a], List[b]] = {
    val b1 = new ListBuffer[a]
    val b2 = new ListBuffer[b]
    var xc = xs
    while (!xc.isEmpty) {
      b1 += xc.head._1
      b2 += xc.head._2
      xc = xc.tail
    }
    Pair(b1.toList, b2.toList)
  }

  /** Converts an iterator to a list
   *
   *  @param it the iterator to convert
   *  @return a list that contains the elements returned by successive
   *  calls to <code>it.next</code>
   */
  def fromIterator[a](it: Iterator[a]): List[a] = it.toList;

  /** Converts an array into a list.
   *
   *  @param arr the array to convert
   *  @return a list that contains the same elements than <code>arr</code>
   *           in the same order
   */
  def fromArray[a](arr: Array[a]): List[a] = fromArray(arr, 0, arr.length);

  /** Converts a range of an array into a list.
   *
   *  @param arr the array to convert
   *  @param start the first index to consider
   *  @param len the lenght of the range to convert
   *  @return a list that contains the same elements than <code>arr</code>
   *           in the same order
   */
  def fromArray[a](arr: Array[a], start: Int, len: Int): List[a] = {
    var res: List[a] = Nil;
    var i = start + len;
    while (i > start) {
      i = i - 1;
      res = arr(i) :: res;
    }
    res
  }

  /** Parses a string which contains substrings separated by a
   *
   *  separator character and returns a list of all substrings.
   *  @param str the string to parse
   *  @param separator the separator character
   *  @return the list of substrings
   */
  def fromString(str: String, separator: Char): List[String] = {
    var words: List[String] = List();
    var pos = str.length();
    while (pos > 0) {
      val pos1 = str.lastIndexOf(separator, pos - 1);
      if (pos1 + 1 < pos)
	words = str.substring(pos1 + 1, pos) :: words;
      pos = pos1
    }
    words
  }

  /** Returns the given string as a list of characters.
   *
   *  @param str the string to convert.
   *  @return the string as a list of characters.
   */
  def fromString(str: String): List[Char] =
    Iterator.fromString(str).toList;

  /** Returns the given list of characters as a string.
   *
   *  @param xs the list to convert.
   *  @return the list in form of a string.
   */
  def toString(xs: List[Char]): String = {
    val sb = new StringBuilder();
    var xc = xs;
    while (!xc.isEmpty) {
      sb.append(xc.head);
      xc = xc.tail;
    }
    sb.toString()
  }

  /** Like xs map f, but returns xs unchanged if function `f' maps all elements to themselves
   */
  def mapConserve[a <: AnyRef](xs: List[a])(f: a => a): List[a] = {
    def loop(ys: List[a]): List[a] =
      if (ys.isEmpty) xs
      else {
        val head0 = ys.head
        val head1 = f(head0)
        if (head1 eq head0) {
          loop(ys.tail)
        } else {
          val ys1 = head1 :: mapConserve(ys.tail)(f)
          if (xs eq ys) ys1
          else {
            val b = new ListBuffer[a]
            var xc = xs
            while (xc ne ys) {
              b += xc.head
              xc = xc.tail
            }
            b.prependToList(ys1)
          }
        }
      }
    loop(xs)
  }

  /** Returns the list resulting from applying the given function <code>f</code> to
   *  corresponding elements of the argument lists.
   *
   *  @param f function to apply to each pair of elements.
   *  @return <code>[f(a0,b0), ..., f(an,bn)]</code> if the lists are
   *          <code>[a0, ..., ak]</code>, <code>[b0, ..., bl]</code> and
   *          <code>n = min(k,l)</code>
   */
  def map2[a,b,c](xs: List[a], ys: List[b])(f: (a, b) => c): List[c] = {
    val b = new ListBuffer[c]
    var xc = xs
    var yc = ys
    while (!xc.isEmpty && !yc.isEmpty) {
      b += f(xc.head, yc.head)
      xc = xc.tail
      yc = yc.tail
    }
    b.toList
  }

  /** Returns the list resulting from applying the given function <code>f</code> to
   *  corresponding elements of the argument lists.
   *
   *  @param f function to apply to each pair of elements.
   *  @return <code>[f(a0,b0,c0), ..., f(an,bn,cn)]</code> if the lists are
   *          <code>[a0, ..., ak]</code>, <code>[b0, ..., bl]</code>, <code>[c0, ..., cm]</code> and
   *          <code>n = min(k,l,m)</code>
   */
  def map3[a,b,c, d](xs: List[a], ys: List[b], zs: List[c])(f: (a, b, c) => d): List[d] = {
    val b = new ListBuffer[d]
    var xc = xs
    var yc = ys
    var zc = zs
    while (!xc.isEmpty && !yc.isEmpty && !zc.isEmpty) {
      b += f(xc.head, yc.head, zc.head)
      xc = xc.tail
      yc = yc.tail
      zc = zc.tail
    }
    b.toList
  }

  /** Tests whether the given predicate <code>p</code> holds
   *  for all corresponding elements of the argument lists.
   *
   *  @param p function to apply to each pair of elements.
   *  @return <code>n == 0 || (p(a0,b0) &amp;&amp; ... &amp;&amp; p(an,bn))]</code> if the lists are
   *          <code>[a0, ..., ak]</code>, <code>[b0, ..., bl]</code> and
   *          <code>m = min(k,l)</code>
   */
  def forall2[a,b](xs: List[a], ys: List[b])(f: (a, b) => boolean): boolean = {
    var xc = xs
    var yc = ys
    while (!xc.isEmpty && !yc.isEmpty) {
      if (!f(xc.head, yc.head)) return false
      xc = xc.tail
      yc = yc.tail
    }
    true
  }

  /** Tests whether the given predicate <code>p</code> holds
   *  for some corresponding elements of the argument lists.
   *
   *  @param p function to apply to each pair of elements.
   *  @return <code>n != 0 &amp;&amp; (p(a0,b0) || ... || p(an,bn))]</code> if the lists are
   *          <code>[a0, ..., ak]</code>, <code>[b0, ..., bl]</code> and
   *          <code>m = min(k,l)</code>
   */
  def exists2[a,b](xs: List[a], ys: List[b])(f: (a, b) => boolean): boolean = {
    var xc = xs
    var yc = ys
    while (!xc.isEmpty && !yc.isEmpty) {
      if (f(xc.head, yc.head)) return true
      xc = xc.tail
      yc = yc.tail
    }
    false
  }

  /** Transposes a list of lists.
   *  pre: All element lists have the same length.
   */
  def transpose[a](xss: List[List[a]]): List[List[a]] =
    if (xss.head.isEmpty) List()
    else (xss map (xs => xs.head)) :: transpose(xss map (xs => xs.tail));

  /** Lists with ordered elements are ordered
  implicit def list2ordered[a <% Ordered[a]](x: List[a]): Ordered[List[a]] = new Ordered[List[a]] {
    def compare [b >: List[a] <% Ordered[b]](y: b): Int = y match {
      case y1: List[a] => compareLists(x, y1);
      case _ => -(y compare x)
    }
    private def compareLists(xs: List[a], ys: List[a]): Int = {
      if (xs.isEmpty && ys.isEmpty) 0
      else if (xs.isEmpty) -1
      else if (ys.isEmpty) 1
      else {
        val s = xs.head compare ys.head;
        if (s != 0) s
        else compareLists(xs.tail, ys.tail)
      }
    }
  }
   */
}

/** A class representing an ordered collection of elements of type
 *  <code>a</code>. This class comes with two implementing case
 *  classes <code>scala.Nil</code> and <code>scala.::</code> that
 *  implement the abstract members <code>isEmpty</code>,
 *  <code>head</code> and <code>tail</code>.
 *
 *  @author  Martin Odersky and others
 *  @version 1.0, 16/07/2003
 */
sealed abstract class List[+a] extends Seq[a] with CaseClass {

  /** Returns true if the list does not contain any elements.
   *  @return true, iff the list is empty.
   */
  override def isEmpty: Boolean;

  /** Returns this first element of the list.
   *  @return the first element of this list.
   *  @throws <code>java.lang.RuntimeException</code> if the list is empty.
   */
  def head: a;

  /** Returns this list without its first element.
   *  @return this list without its first element.
   *  @throws <code>java.lang.RuntimeException</code> if the list is empty.
   */
  def tail: List[a];

  /** Add an element <code>x</code> at the beginning of this list.
   *  <p/>
   *  Ex:<br/>
   *  <code>1 :: [2, 3] = [2, 3].::(1) = [1, 2, 3]</code>.
   *  @param x the element to append.
   *  @return the list with <code>x</code> appended at the beginning.
   */
  def ::[b >: a] (x: b): List[b] =
    new scala.::(x, this);

  /** Returns a list resulting from the concatenation of the given
   *  list <code>prefix</code> and this list.
   *  <p/>
   *  Ex:<br/>
   *  <code>[1, 2] ::: [3, 4] = [3, 4].:::([1, 2]) = [1, 2, 3, 4]</code>.
   *  @param prefix the list to concatenate at the beginning of this list.
   *  @return the concatenation of the two lists.
   */
  def :::[b >: a](prefix: List[b]): List[b] =
    if (isEmpty) prefix
    else {
      val b = new ListBuffer[b]
      var those = prefix
      while (!those.isEmpty) {
        b += those.head
        those = those.tail
      }
      b.prependToList(this)
    }

  /** Reverse the given prefix and append the current list to that.
   *  This function is equivalent to an application of <code>reverse</code>
   *  on the prefix followed by a call to <code>:::</code>, but more
   *  efficient (and tail recursive).
   *  @param prefix the prefix to reverse and then prepend
   *  @return the concatenation of the reversed prefix and the current list.
   */
  def reverse_:::[b >: a](prefix: List[b]): List[b] = prefix match {
    case Nil => this
    case head :: tail => tail.reverse_:::(head :: this)
  }

  /** Returns the number of elements in the list.
   *
   *  @return the number of elements in the list.
   */
  def length: Int = {
    var these = this;
    var len = 0;
    while (!these.isEmpty) {
      len = len + 1;
      these = these.tail
    }
    len
  }

  /** Creates a list with all indices in the list. This is
   *  equivalent to a call to <code>List.range(0, xs.length)</code>.
   *
   *  @return a list of all indices in the list.
   */
  def indices: List[Int] = {
    val b = new ListBuffer[Int]
    var i = 0
    var these = this
    while (!these.isEmpty) {
      b += i
      i = i + 1
      these = these.tail
    }
    b.toList
  }

  /** Returns the elements in the list as an iterator
   *
   *  @return an iterator on the list elements.
   */
  def elements: Iterator[a] = new Iterator[a] {
    var these = List.this;
    def hasNext: Boolean = !these.isEmpty;
    def next: a =
      if (!hasNext)
        error("next on empty Iterator")
      else {
        val result = these.head; these = these.tail; result
      }
    override def toList: List[a] = these;
  }

  /** Overrides the method in Iterable for efficiency.
   *
   *  @return  the list itself
   */
  override def toList: List[a] = this;

  /** Returns the list without its last element.
   *
   *  @return the list without its last element.
   *  @throws <code>java.lang.RuntimeException</code> if the list is empty.
   */
  def init: List[a] =
    if (isEmpty) error("Nil.init")
    else {
      val b = new ListBuffer[a]
      var elem = head
      var next = tail
      while (!next.isEmpty) {
        b += elem
        elem = next.head
        next = next.tail
      }
      b.toList
    }

  /** Returns the last element of this list.
   *
   *  @return the last element of the list.
   *  @throws <code>java.lang.RuntimeException</code> if the list is empty.
   */
  def last: a =
    if (isEmpty) error("Nil.last")
    else if (tail.isEmpty) head
    else tail.last;

  /** Returns the <code>n</code> first elements of this list.
   *
   *  @param n the number of elements to take.
   *  @return the <code>n</code> first elements of this list.
   */
  override def take(n: Int): List[a] = {
    val b = new ListBuffer[a]
    var i = 0
    var these = this
    while (!these.isEmpty && i < n) {
      i = i + 1
      b += these.head
      these = these.tail
    }
    b.toList
  }

  /** Returns the list without its <code>n</code> first elements.
   *
   *  @param n the number of elements to drop.
   *  @return the list without its <code>n</code> first elements.
   */
  override def drop(n: Int): List[a] =
    if (n == 0 || isEmpty) this
    else (tail drop (n-1));

  /** Returns the rightmost <code>n</code> elements from this list.
   *
   *  @param n the number of elements to take
   *  @return the suffix of length <code>n</code> of the list
   *  @throws <code>java.lang.RuntimeException</code> if the list is too short.
   */
  def takeRight(n: Int): List[a] = {
    def loop(lead: List[a], lag: List[a]): List[a] = lead match {
      case Nil => lag
      case _ :: tail => loop(tail, lag.tail)
    }
    loop(drop(n), this)
  }

  /** Returns the list wihout its rightmost <code>n</code> elements.
   *
   *  @param n the number of elements to take
   *  @return the suffix of length <code>n</code> of the list
   *  @throws <code>java.lang.RuntimeException</code> if the list is too short.
   */
  def dropRight(n: Int): List[a] = {
    def loop(lead: List[a], lag: List[a]): List[a] = lead match {
      case Nil => Nil
      case _ :: tail => lag.head :: loop(tail, lag.tail)
    }
    loop(drop(n), this)
  }

  /** Split the list at a given point and return the two parts thus
   *  created.
   *
   *  @param n the position at which to split
   *  @return a pair of lists composed of the first <code>n</code>
   *          elements, and the other elements.
   */
  def splitAt(n: Int): Pair[List[a], List[a]] = {
    val b = new ListBuffer[a]
    var i = 0
    var these = this;
    while (!these.isEmpty && i < n) {
      i = i + 1
      b += these.head
      these = these.tail
    }
    Pair(b.toList, these)
  }

  /** Returns the longest prefix of this list whose elements satisfy
   *  the predicate <code>p</code>.
   *
   *  @param p the test predicate.
   *  @return the longest prefix of this list whose elements satisfy
   *  the predicate <code>p</code>.
   */
  def takeWhile(p: a => Boolean): List[a] = {
    val b = new ListBuffer[a]
    var these = this
    while (!these.isEmpty && p(these.head)) {
      b += these.head
      these = these.tail
    }
    b.toList
  }

  /** Returns the longest suffix of this list whose first element
   *  does not satisfy the predicate <code>p</code>.
   *
   *  @param p the test predicate.
   *  @return the longest suffix of the list whose first element
   *          does not satisfy the predicate <code>p</code>.
   */
  def dropWhile(p: a => Boolean): List[a] =
    if (isEmpty || !p(head)) this
    else tail dropWhile p;

  /** Returns the longest prefix of the list whose elements all satisfy
   *  the given predicate, and the rest of the list.
   *
   *  @param p the test predicate
   *  @return a pair consisting of the longest prefix of the list whose
   *  elements all satisfy <code>p</code>, and the rest of the list.
   */
  def span(p: a => Boolean): Pair[List[a], List[a]] = {
    val b = new ListBuffer[a]
    var these = this
    while (!these.isEmpty && p(these.head)) {
      b += these.head
      these = these.tail
    }
    Pair(b.toList, these)
  }

  /** Like <code>span</code> but with the predicate inverted.
   */
  def break(p: a => Boolean): Pair[List[a], List[a]] = span { x => !p(x) }

  /** Returns the <code>n</code>-th element of this list. The first element
   *  (head of the list) is at position 0.
   *
   *  @param n index of the element to return
   *  @return the element at position <code>n</code> in this list.
   *  @throws <code>java.lang.RuntimeException</code> if the list is too short.
   */
  def apply(n: Int): a = drop(n).head;

  /** Returns the list resulting from applying the given function <code>f</code> to each
   *  element of this list.
   *
   *  @param f function to apply to each element.
   *  @return <code>[f(a0), ..., f(an)]</code> if this list is <code>[a0, ..., an]</code>.
   */
  def map[b](f: a => b): List[b] = {
    val b = new ListBuffer[b]
    var these = this
    while (!these.isEmpty) {
      b += f(these.head)
      these = these.tail
    }
    b.toList
  }

  /** Apply a function to all the elements of the list, and return the
   *  reversed list of results. This is equivalent to a call to <code>map</code>
   *  followed by a call to <code>reverse</code>, but more efficient.
   *
   *  @param f the function to apply to each elements.
   *  @return the reversed list of results.
   */
  def reverseMap[b](f: a => b): List[b] = {
    def loop(l: List[a], res: List[b]): List[b] = l match {
      case Nil => res
      case head :: tail => loop(tail, f(head) :: res)
    }
    loop(this, Nil)
  }

  /** Apply the given function <code>f</code> to each element of this list
   *  (while respecting the order of the elements).
   *
   *  @param f the treatment to apply to each element.
   */
  override def foreach(f: a => Unit): Unit = {
    var these = this;
    while (!these.isEmpty) {
      f(these.head)
      these = these.tail
    }
  }

  /** Returns all the elements of this list that satisfy the
   *  predicate <code>p</code>. The order of the elements is preserved.
   *
   *  @param p the redicate used to filter the list.
   *  @return the elements of this list satisfying <code>p</code>.
   */
  def filter(p: a => Boolean): List[a] = {
    // return same list if all elements satisfy p
    var these = this
    while (!these.isEmpty && p(these.head)) {
      these = these.tail
    }
    if (these.isEmpty) this
    else {
      val b = new ListBuffer[a]
      var these1 = this;
      while (these1 ne these) {
        b += these1.head
        these1 = these1.tail
      }
      while (!these.isEmpty) {
        if (p(these.head)) b += these.head
        these = these.tail
      }
      b.toList
    }
  }

  /** Removes all elements of the list which satisfy the predicate
   *  <code>p</code>. This is like <code>filter</code> with the
   *  predicate inversed.
   *
   *  @param p the predicate to use to test elements
   *  @return the list without all elements which satisfy <code>p</code>
   */
  def remove(p: a => Boolean): List[a] = filter (x => !p(x));

  /** Partition the list in two sub-lists according to a predicate.
   *
   *  @param p the predicate on which to partition
   *  @return a pair of lists: the list of all elements which satisfy
   *  <code>p</code> and the list of all elements which do not. The
   *  relative order of the elements in the sub-lists is the same as in
   *  the original list.
   */
  def partition(p: a => Boolean): Pair[List[a], List[a]] = {
    val btrue = new ListBuffer[a]
    val bfalse = new ListBuffer[a]
    var these = this
    while (!these.isEmpty) {
      (if (p(these.head)) btrue else bfalse) += these.head
      these = these.tail
    }
    Pair(btrue.toList, bfalse.toList)
  }

  /** Sort the list according to the comparison function
   *  <code>&lt;(e1: a, e2: a) =&gt; Boolean</code>,
   *  which should be true iff e1 is smaller than e2.
   *  Note: The current implementation is inefficent for
   *  already sorted lists.
   *
   *  @param lt the comparison function
   *  @return a list sorted according to the comparison function
   *          <code>&lt;(e1: a, e2: a) =&gt; Boolean</code>.
   */
  def sort(lt : (a,a) => Boolean): List[a] = {
    def sort_1(smaller: List[a], acc: List[a]): List[a] =
      smaller match {
        case Nil =>
          acc
        case List(x) =>
          x::acc
        case List(x, y) =>
          if (lt(x, y)) x::(y::acc) else y::x::acc
        case List(x, y, z) =>
          if (lt(x, y)) {
            if (lt(y, z)) x::y::z::acc
            else if (lt(x, z)) x::z::y::acc
            else z::x::y::acc
          } else if (lt(x, z)) y::x::z::acc
          else if (lt(z, y)) z::y::x::acc
          else y::z::x::acc
        case hd1::hd2::hd3::tail => {
          val List(x, y, z) = sort_1(hd1::hd2::hd3::Nil, Nil);
          val Pair(small, large) = tail.partition((e2) => lt(e2, y));
          sort_1(x::small, y::sort_1(z::large, acc))
        }
      }
    this match {
      case Nil =>
        this
      case List(x) =>
        this
      case List(x, y) =>
        if (lt(x, y)) this else y::x::Nil
      case List(x, y, z) =>
        if (lt(x, y)) {
          if (lt(y, z)) this
          else if (lt(x, z)) x::z::y::Nil
          else z::x::y::Nil
        } else if (lt(x, z)) y::x::z::Nil
        else if (lt(z, y)) z::y::x::Nil
        else y::z::x::Nil
      case hd1::hd2::hd3::tail => {
        val List(x, y, z) = sort_1(hd1::hd2::hd3::Nil, Nil);
        val Pair(small,large) =  tail.partition((e2) => lt(e2, y));
        sort_1(x::small, y::sort_1(z::large, Nil));
      }
    }
  }


  /** Count the number of elements in the list which satisfy a predicate.
   *
   *  @param p the predicate for which to count
   *  @return the number of elements satisfying the predicate <code>p</code>.
   */
  def count(p: a => Boolean): Int = {
    var cnt = 0
    var these = this
    while (!these.isEmpty) {
      if (p(these.head)) cnt = cnt + 1
      these = these.tail
    }
    cnt
  }

  /** Tests if the predicate <code>p</code> is satisfied by all elements
   *  in this list.
   *
   *  @param p the test predicate.
   *  @return True iff all elements of this list satisfy the predicate <code>p</code>.
   */
  override def forall(p: a => Boolean): Boolean = {
    var these = this
    while (!these.isEmpty) {
      if (!p(these.head)) return false
      these = these.tail
    }
    true
  }

  /** Tests the existence in this list of an element that satisfies the predicate
   * <code>p</code>.
   *
   *  @param p the test predicate.
   *  @return true iff there exists an element in this list that satisfies
   *  the predicate <code>p</code>.
   */
  override def exists(p: a => Boolean): Boolean = {
    var these = this
    while (!these.isEmpty) {
      if (p(these.head)) return true
      these = these.tail
    }
    false
  }

  /** Tests if the given value <code>elem</code> is a member of this
   *  iterable object.
   *
   *  @param elem element whose membership has to be tested.
   *  @return True iff there is an element of this list which is
   *  equal (w.r.t. <code>==</code>) to <code>elem</code>.
   */
  def contains(elem: Any): boolean = exists (.==(elem))

  /** Find and return the first element of the list satisfying a
   *  predicate, if any.
   *
   *  @param p the predicate
   *  @return the first element in the list satisfying <code>p</code>,
   *  or <code>None</code> if none exists.
   */
  override def find(p: a => Boolean): Option[a] = {
    var these = this
    while (!these.isEmpty) {
      if (p(these.head)) return Some(these.head)
      these = these.tail
    }
    None
  }

  /** Combines the elements of this list together using the binary
   *  operator <code>op</code>, from left to right, and starting with
   *  the value <code>z</code>.
   *
   *  @return <code>op(... (op(op(z,a0),a1) ...), an)</code> if the list
   *  is <code>[a0, a1, ..., an]</code>.
   */
  override def foldLeft[b](z: b)(f: (b, a) => b): b = {
    var acc = z
    var these = this
    while (!these.isEmpty) {
      acc = f(acc, these.head)
      these = these.tail
    }
    acc
  }

  /** Combines the elements of this list together using the binary
   *  operator <code>op</code>, from rigth to left, and starting with
   *  the value <code>z</code>.
   *
   *  @return <code>a0 op (... op (an op z)...)</code> if the list
   *  is <code>[a0, a1, ..., an]</code>.
   */
  override def foldRight[b](z: b)(f: (a, b) => b): b = this match {
    case Nil => z
    case x :: xs => f(x, xs.foldRight(z)(f))
  }

  def reduceLeft[b >: a](f: (b, b) => b): b = this match {
    case Nil => error("Nil.reduceLeft")
    case x :: xs => ((xs: List[b]) foldLeft (x: b))(f)
  }

  def reduceRight[b >: a](f: (b, b) => b): b = this match {
    case Nil => error("Nil.reduceRight")
    case x :: Nil => x: b
    case x :: xs => f(x, xs reduceRight f)
  }

  /** Applies the given function <code>f</code> to each element of
   *  this list, then concatenates the results.
   *
   *  @param f the function to apply on each element.
   *  @return <code>f(a0) ::: ... ::: f(an)</code> if this list is
   *  <code>[a0, ..., an]</code>.
   */
  def flatMap[b](f: a => List[b]): List[b] = {
    val b = new ListBuffer[b]
    var these = this
    while (!these.isEmpty) {
      var those = f(these.head)
      while (!those.isEmpty) {
        b += those.head
        those = those.tail
      }
      these = these.tail
    }
    b.toList
  }

  /** Reverses the elements of this list.
   *  <p/>
   *  Ex: <br/>
   *  <code>[1, 2, 3] reverse = [3, 2, 1]</code>.
   *
   *  @return the elements of this list in reverse order.
   */
  def reverse: List[a] =
    foldLeft(Nil : List[a])((xs, x) => x :: xs);

  /** Returns a list formed from this list and the specified list
   *  <code>that</code> by associating each element of the former with
   *  the element at the same position in the latter.
   *
   *  @param <code>that</code> must have the same length as the self list.
   *  @return <code>[(a0,b0), ..., (an,bn)]</code> when
   *  <code>[a0, ..., an] zip [b0, ..., bn]</code> is invoked.
   */
  def zip[b](that: List[b]): List[Pair[a,b]] = {
    val b = new ListBuffer[Pair[a, b]]
    var these = this
    var those = that
    while (!these.isEmpty && !those.isEmpty) {
      b += Pair(these.head, those.head)
      these = these.tail
      those = those.tail
    }
    b.toList
  }

   /** Return an list that pairs each element of this list
    *  with its index, counting from 0.
    *
    *  @param   <code>start</code> the index of the first element
    *
    *  @return  an iterator yielding <code>(a0,0), (a0,1)...</code>
    *           where <code>ai</code> are the elements from this iterator.
    */
  def zipWithIndex = {
    val b = new ListBuffer[Pair[a,int]]
    var these = this
    var idx = 0

    while(!these.isEmpty) {
      b += Pair(these.head, idx)
      these = these.tail
      idx = idx + 1
    }

    b.toList
  }

  /** Returns a list formed from this list and the specified list
   *  <code>that</code> by associating each element of the former with
   *  the element at the same position in the latter.
   *
   *  @param <code>that</code> may have a different length as the self list.
   *  @param <code>thisElem</code> is used to fill up the resulting list if
   *  the self list is shorter than <code>that</code>
   *  @param <code>thatElem</code> is used to fill up the resulting list if
   *  <code>that</code> is shorter than the self list
   *  @return <code>[(a0,b0), ..., (an,bn), (elem,bn+1), ..., (elem,bm)]</code>
   *  when <code>[a0, ..., an] zip [b0, ..., bm]</code> is invoked where
   *  <code>m &gt; n</code>.
   */
  def zipAll[b, c >: a, d >: b](that: List[b], thisElem: c, thatElem: d): List[Pair[c,d]] = {
    val b = new ListBuffer[Pair[c, d]]
    var these = this
    var those = that
    while (!these.isEmpty && !those.isEmpty) {
      b += Pair(these.head, those.head)
      these = these.tail
      those = those.tail
    }
    while (!these.isEmpty) {
      b += Pair(these.head, thatElem)
      these = these.tail
    }
    while (!those.isEmpty) {
      b += Pair(thisElem, those.head)
      those = those.tail
    }
    b.toList
  }

  /** Computes the union of this list and the given list
   *  <code>that</code>.
   *
   *  @param that the list of elements to add to the list.
   *  @return a list without doubles containing the elements of this
   *  list and those of the given list <code>that</code>.
   */
  def union[b >: a](that: List[b]): List[b] = {
    val b = new ListBuffer[b]
    var these = this
    while (!these.isEmpty) {
      if (!that.contains(these.head)) b += these.head
      these = these.tail
    }
    b.prependToList(that)
  }

  /** Computes the difference between this list and the given list
   *  <code>that</code>.
   *
   *  @param that the list of elements to remove from this list.
   *  @return this list without the elements of the given list <code>that</code>.
   */
  def diff[b >: a](that: List[b]): List[b] = {
    val b = new ListBuffer[b]
    var these = this
    while (!these.isEmpty) {
      if (!that.contains(these.head)) b += these.head
      these = these.tail
    }
    b.toList
  }

  /** Computes the intersection between this list and the given list
   *  <code>that</code>.
   *
   *  @param that the list to intersect.
   *  @return the list of elements contained both in this list and
   *          in the given list <code>that</code>.
   */
  def intersect[b >: a](that: List[b]): List[b] = filter(x => that contains x);

  /** Removes redundant elements from the list. Uses the method <code>==</code>
   *  to decide if two elements are identical.
   *
   *  @return the list without doubles.
   */
  def removeDuplicates: List[a] = {
    val b = new ListBuffer[a]
    var these = this
    while (!these.isEmpty) {
      if (!these.tail.contains(these.head)) b += these.head
      these = these.tail
    }
    b.toList
  }

  override protected def stringPrefix: String = "List"
}

/** The empty list.
 *
 *  @author  Martin Odersky
 *  @version 1.0, 15/07/2003
 */
[SerialVersionUID(0 - 8256821097970055419L)]
case object Nil extends List[Nothing] {
  override def isEmpty = true;
  def head: All = error("head of empty list");
  def tail: List[Nothing] = error("tail of empty list");
}

/** A non empty list characterized by a head and a tail.
 *
 *  @author  Martin Odersky
 *  @version 1.0, 15/07/2003
 */
[SerialVersionUID(0L - 8476791151983527571L)]
final case class ::[b](hd: b, private[scala] var tl: List[b]) extends List[b] {
  def head = hd;
  def tail = tl;
  override def isEmpty: boolean = false;
}

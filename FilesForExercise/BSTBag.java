
// Student ID: 3118197
// Name: WEI-TSUNG CHENG

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


// Bag 允許重複元素；此處用「一般二元搜尋樹 + 每個元素的次數」實作。
// E 必須可比較，才能決定往左或往右搜尋；CountedElement 的比較只看值，不看次數。
// 每個不同的值只佔一個節點：左子樹的值較小，右子樹的值較大。
// 此樹不會自動平衡。若樹高為 h，新增、搜尋、刪除為 O(h)；最壞可能退化成鏈狀。
public class BSTBag<E extends Comparable<E>> implements Bag<E> {

    // 節點將元素及次數包在 CountedElement 中，並保存左右子節點的參照。
    // static 表示節點不需要隱含持有外層 BSTBag 物件；此 E 是節點自己宣告的型別參數。
    private static class Node<E extends Comparable<E>> {
        private CountedElement<E> element;
        private Node<E> left;
        private Node<E> right;

        // 新節點起初沒有子節點；實際接到哪個父節點，由新增操作決定。
        Node(CountedElement<E> e) {
            this.element = e;
            this.left = null;
            this.right = null;
        }
    }

    // root 是整棵樹的入口；沒有自訂建構子時，參照欄位預設為 null。
    private Node<E> root;
    // size 計算所有出現次數，不是不同元素數；例如 ant 三次佔一個節點，但 size 為 3。
    // int 欄位預設為 0；維持 size 等於所有節點 count 的總和。
    private int size;

    // 直接查詢總大小即可判斷是否為空，不需要走訪樹。
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // 回傳已維護的總次數，因此查詢為 O(1)。
    @Override
    public int size() {
        return size;
    }

    // 新增一次出現：已有節點就增加 count，否則建立新節點。
    @Override
    public void add(E element) {
        // 本實作忽略 null，避免後續 compareTo 無法比較。
        if (element == null) {
            return;
        }

        // 單參數建構子會將 count 設為 1；如果找到舊節點，就不需要插入這個包裝物件。
        CountedElement<E> newElement = new CountedElement<>(element);
        // 空樹的第一個元素直接成為根；更新總大小後結束。
        if (root == null) {
            root = new Node<>(newElement);
            size += 1;
            return;
        }

        // current 負責往下搜尋；parent 保存上一個非空節點，供找到空位置後接回新節點。
        Node<E> current = root;
        Node<E> parent = null;
        int compare = 0;

        // 每輪都依比較結果排除一側子樹，不需要掃描所有節點。
        while (current != null) {
            parent = current;
            compare = newElement.compareTo(current.element);
            // compare 為 0 表示同一個排序值：增加次數即可，不再建立重複節點。
            if (compare == 0) {
                current.element.setCount(current.element.getCount() + 1);
                size += 1;
                return;
            // 新值比目前節點大就往右，否則往左。
                } else if (compare > 0) {
                current = current.right;
            } else {
                current = current.left;
            }
        }

        // 走到 null 表示找不到既有值；compare 保留與 parent 最後一次比較的結果。
        // 依該結果將新節點接到 parent 的右側或左側，再增加總大小。
        Node<E> newNode = new Node<>(newElement);
        if (compare > 0) {
            parent.right = newNode;
        } else {
            parent.left = newNode;
        }
        size += 1;
    }

    // 公開刪除入口：每次只移除一個 occurrence，而非一次刪除該值的所有副本。
    @Override
    public void remove(E element) {
        // null 或空樹不需處理，也不改變 size。
        if (element == null || root == null)  {
            return;
        }
        // target 只是比較用的包裝，count 不參與搜尋。
        CountedElement<E> target = new CountedElement<>(element);
        // 刪除可能改變根（例如根只剩一個子節點），所以必須接住新的子樹根。
        root = recursiveRemove(root, target);
    }

    // Recursive Remove Target
    // 回傳「刪除後的子樹根」，讓呼叫端更新自己的 left、right 或整棵樹的 root。
    private Node<E> recursiveRemove(Node<E> node, CountedElement<E> target) {
        // 搜尋到空子樹表示目標不存在；返回 null，且不減少 size。
        if (node == null) {
            return null;
        }

        int compare = target.compareTo(node.element);

        if (compare == 0) {
            // 已找到目標；依次數判斷只減少 count，或需要真正移除節點。
            int count = node.element.getCount();

            // 還有其他副本時，樹形完全不變；count 與 size 各減 1 後返回原節點。
            // only reduce count if count > 1
            if (count > 1) {
                 node.element.setCount(count - 1);
                 size -= 1;
                 return node;

            // 最後一個副本被刪除時，節點也必須移除；以下所有分支只扣一次 size。
            // if count = 1, reduce count and remove the node
            } else {
                 size -= 1;
                 // 情況一：葉節點。回傳 null，讓父節點斷開指向此節點的連結。
                 // if node has no children, return null
                 if (node.left == null && node.right == null) {
                     return null;
                 }
                 // 情況二：只有一側子樹。將那側子節點往上接，保留其全部後代。
                 // if node has only one child, replace with child
                 if (node.left == null) {
                     return node.right;
                 }

                 if (node.right == null) {
                     return node.left;
                 }
                 // 情況三：有兩側子樹。用右子樹最小值取代目標，仍能維持 BST 的排序關係。
                 // if node has two children
                 // Find the smallest node in the right subtree (in-order successor).

                 // get the smallest node in the right subtree
                 Node<E> successor = findMin(node.right);
                 // Copy the data from the successor node into the node to be deleted.
                 // 必須一起搬移後繼的值與完整 count，否則會遺失後繼元素的重複次數。
                 node.element = new CountedElement<>(
                    successor.element.getElement(),
                    successor.element.getCount()
                 );
                 // 後繼的資料已搬上來，現在只移除它原來的節點，不能再次扣 size。
                 // 例如刪除 x 一次並搬移 y 三次，整個 Bag 只少了 x 那一次。
                 node.right = deleteMin(node.right);
            }

        // 目標較大時只處理右子樹；較小時只處理左子樹。
        // 接回遞迴結果，因為子樹根可能已刪除或換成其子節點。
        } else if (compare > 0) {
            node.right = recursiveRemove(node.right, target);
        } else {
            node.left = recursiveRemove(node.left, target);
        }

        // 將更新完成的子樹根交回上一層；上一層再把它接回自己的樹。
        return node;
    }

    // Find the smallest node in the right subtree (in-order successor).
    // BST 的最小值一定在最左端；此方法只查詢，不修改樹或總大小。
    private Node<E> findMin(Node<E> node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Delete the smallest node in the right subtree (in-order successor).
    // 此輔助方法移除的是整個最小節點，而非只減少它的一次出現。
    // 僅供後繼搬移後使用，因此不修改 size，避免重複扣除。
    private Node<E> deleteMin(Node<E> node) {
        if (node == null) {
            return null;
        }
        // 沒有左子節點即找到最小節點；接回其右子樹，避免遺失右側後代。
        if (node.left == null) {
            return node.right;
        }
        // 最小值還在左側；遞迴刪除後，更新左子樹入口並保留目前節點。
        node.left = deleteMin(node.left);
        return node;
    }

    // 移除 Bag 對整棵樹的入口並重設總數；不必逐個刪除節點。
    // 沒有其他參照的物件之後由垃圾回收處理，此方法本身為 O(1)。
    @Override
    public void clear() {
        root = null;
        size = 0;

    }

    // 沿 BST 搜尋路徑判斷是否存在，不需要展開每個元素的重複次數。
    // 注意：此方法沒有像 add/remove 一樣檢查 null；非空樹上傳入 null 可能拋出例外。
    @Override
    public boolean contains(E element) {
        if (root == null) {
            return false;
        }
        Node<E> current = root;
        CountedElement<E> target = new CountedElement<>(element);

        while (current != null) {
            int compare = target.compareTo(current.element);
            if (compare == 0) {
               return true;
            } else if (compare > 0) {
                current = current.right;
            } else {
                current = current.left;
            }
        }
        // 走出迴圈表示搜尋路徑已到空位置，找不到目標。
        return false;
    }

    // Bag 相等要求元素及各自次數相同，與節點形狀或插入順序無關。
    // 此處實作 equals(Bag<E>)，不是覆寫 Object.equals(Object)。
    @Override
    public boolean equals(Bag<E> that) {
        // null 不是相等的 Bag；總次數不同也能立即判定不相等。
        if (that == null) {
            return false;
        }
        if (this.size() != that.size()) {
            return false;
        }

        // 透過本 Bag 的 iterator 建立次數表：每個副本都會被回傳一次，因此逐次加 1。
        // HashMap 使用 equals/hashCode 判斷鍵，而 BST 使用 compareTo；元素應保持兩者語意一致。
        Map<E, Integer> thisMap = new HashMap<>();
        for (E e: this) {
            thisMap.put(e, thisMap.getOrDefault(e, 0) + 1);
        }

        // 走訪另一個 Bag，每遇到一個副本就扣掉一次可配對次數。
        for (E e: that) {
            Integer count = thisMap.get(e);
            // 沒有此鍵，或此值的可配對次數已用完，代表元素或數量不一致。
            if (count == null || count == 0) {
                return false;
            }
            thisMap.put(e, count - 1);
        }

        // 確認本 Bag 沒有剩餘未配對的元素；全部歸零才代表內容相等。
        for (Integer count: thisMap.values()) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }

    // Bag 繼承 Iterable，因此必須提供 iterator，讓 for-each 能逐個取得元素。
    // 每次建立新的走訪物件，各自保存自己的進度；它們仍參照同一棵樹，並非資料快照。
    @Override
    public Iterator<E> iterator() {
        return new BSTIterator();
    }

    // BSTIterator
    // 使用中序走訪（左子樹、自己、右子樹），依自然順序回傳 E，並保留重複次數。
    // 這是非 static 內部類別，可直接讀取外層 Bag 的 root。
    // 未實作 remove()，其預設行為會拋出 UnsupportedOperationException；
    // forEachRemaining() 可使用 Iterator 的預設實作。走訪途中不應修改 Bag，這裡沒有修改偵測。
    private class BSTIterator implements Iterator<E> {

        // 堆疊保存待回訪節點，取代遞迴呼叫堆疊；空間為 O(h)。
        // final 只固定 stack 參照，仍可 push/pop 改變堆疊內容。
        private final Stack<Node<E>> stack = new LinkedStack<>();
        // 已取得目前節點後，還需要額外回傳幾次相同元素。
        private int repeatCount = 0;
        // 保存目前節點的元素及次數，供重複回傳時使用。
        private CountedElement<E> currentElement = null;

        // 建立 iterator 時先準備最左路徑；空樹不會推入任何節點。
        BSTIterator() {
            pushLeft(root);
        }

        // 從某個子樹入口一路往左推入節點，最小值最後入堆疊、最先彈出。
        // 上方祖先留在堆疊中，等較小的元素處理完成後再取出。
        private void pushLeft(Node<E> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        // 即使堆疊已空，仍可能有目前元素的重複副本；任一條件成立就還有下一筆。
        // 此方法只查狀態，不推進走訪，因此可重複呼叫。
        @Override
        public boolean hasNext() {
            return repeatCount > 0 || !stack.empty();
        }

        // 取得下一個元素並更新走訪進度；全部走訪總時間 O(n)，n 包含重複次數。
        @Override
        public E next() {
            // Iterator 約定：走訪完畢後呼叫 next 必須拋出例外，而不是回傳 null。
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // 優先送出目前元素剩餘的副本，不需要再移動到其他節點。
            if (repeatCount > 0) {
                repeatCount -= 1;
                return currentElement.getElement();
            }

            // 沒有待重複副本時，堆疊頂端就是下一個尚未回傳的最小節點。
            Node<E> node = stack.pop();
            currentElement = node.element;
            // 本次 next 會回傳一次，因此只需記錄 count - 1 次留給後續呼叫。
            repeatCount = currentElement.getCount() - 1;

            // 目前節點之後應走訪其右子樹；先推入右子樹的最左路徑。
            // 即使現在就準備好堆疊，後續 next 仍會先回傳完目前元素的副本。
            if (node.right != null) {
                pushLeft(node.right);
            }

            return currentElement.getElement();
        }
    }

}

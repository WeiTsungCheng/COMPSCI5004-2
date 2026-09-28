# Algorithms & Data Structures (M) — Assessed Exercise 2026

依據 `Ex_2026.pdf` 完成的 Java 字詞處理與 Bag 抽象資料型別作業。本專案包含使用 Java Set 的字詞計數程式，以及使用二元搜尋樹（Binary Search Tree, BST）實作的泛型 Bag。

## 作業內容與檔案

| 部分／檔案 | 用途 |
| --- | --- |
| `Ex_2026.pdf` | 原始作業說明與繳交規定 |
| `FilesForExercise/CountedElement.java` | Part A：儲存元素及其出現次數；比較時只比較元素值 |
| `FilesForExercise/WordProcessor.java` | Part A：使用兩個 `TreeSet` 統計多個文字檔中的字詞 |
| `FilesForExercise/Bag.java` | Part B 提供的 Bag 介面，繼承 `Iterable<E>` |
| `FilesForExercise/BSTBag.java` | Part B：使用一般 BST 實作 Bag，並非 AVL tree |
| `FilesForExercise/Stack.java`、`LinkedStack.java` | 作業提供的堆疊介面及實作，供 iterator 使用 |
| `FilesForExercise/WordProcessor2.java` | Part B 的手動測試程式，包含走訪與刪除示範 |
| `FilesForExercise/file0.txt`～`file2.txt` | 作業提供的範例輸入 |
| `FilesForExercise/algorithm.md` | `add`、`remove`、iterator 的詳細英文演算法說明 |
| `Algorithm 說明部分.pdf` | 現有的演算法 PDF 文件 |

## 編譯與執行

需要可使用 `java` 與 `javac` 的 JDK；本專案已使用 OpenJDK 11.0.29 編譯及執行範例。無須額外套件或建置工具。

在專案根目錄執行：

```sh
cd FilesForExercise
javac CountedElement.java Bag.java Stack.java LinkedStack.java BSTBag.java WordProcessor.java WordProcessor2.java

# Part A：合併統計三個輸入檔
java WordProcessor file0.txt file1.txt file2.txt

# Part B：以 file0.txt 示範 Bag 操作
java WordProcessor2 file0.txt
```

可傳入一個或多個檔案路徑；相對路徑以執行時的工作目錄為準。兩個程式都以 `Scanner` 依空白分隔 token，並使用 `toLowerCase()` 轉成小寫；不會移除標點符號。無法開啟檔案時，程式會拋出包裝 `FileNotFoundException` 的 `RuntimeException`。

### Part A：實際輸出

執行三個範例檔後，目前程式輸出如下，每行最多五筆，依字詞自然順序排列：

```text
ant 4, biscuit 1, box 3, brain 1, camping 1,
cow 5, jumper 1, kettle 3, kilt 1, lathe 1,
present 1, pub 1, table 1, wonder 1, xylophone 1,
yak 1, yoyo 1, zany 1, zeal 2, zoo 3
```

### Part B：測試程式行為

`WordProcessor2 file0.txt` 會先印出 `Size: 9`，再依序輸出 `ant` 三次、`box`、`brain`、`cow`、`zeal` 兩次與 `zoo`，每個元素各佔一行。

接著，程式逐次刪除 `zeal`，使總數從 9 降至 8、7，再印出剩餘元素。最後嘗試刪除不存在的 `dragon`，總數仍為 7。測試迴圈中的 `Does contain ant:` 標籤是現有文字誤植；實際查詢的是 `zeal`。此程式是示範性測試，並未涵蓋 Bag 介面的全部操作與邊界情況。

## 演算法與資料結構

### Part A：使用兩個 Set 模擬 Bag

1. `wordSet` 儲存不同的字詞，`countedWordSet` 儲存其對應的 `CountedElement<String>`。
2. 讀到新字詞時，加入兩個集合，並將次數設為 1。
3. 讀到重複字詞時，走訪 `countedWordSet` 找到對應項目，移除舊項目，再插入次數增加 1 的新項目。
4. 由於 `CountedElement.compareTo()` 只比較元素值，輸出依字詞排序，不依次數排序。

### Part B：BSTBag 的表示方式

每個節點儲存一個 `CountedElement<E>` 及左右子節點。較小元素位於左子樹，較大元素位於右子樹；比較結果相同的元素共用同一節點，以 count 記錄重複次數。

`size` 是所有元素的**總出現次數**，不是節點數。例如加入 `ant` 三次，會建立一個 count 為 3 的節點，Bag 的大小為 3。元素型別必須實作 `Comparable<E>`。

### 新增：add

1. 若輸入為 null，直接返回；若樹為空，建立 count 為 1 的根節點。
2. 從根節點開始比較，依大小往左或往右搜尋。
3. 找到相同元素時，只增加該節點的 count。
4. 若搜尋到空位置，建立 count 為 1 的新節點並接到父節點。
5. 每次成功加入一個元素，總大小增加 1。

### 刪除：remove

每次只移除一個 occurrence。null 或不存在的元素不改變 Bag。

1. 依 BST 順序遞迴搜尋目標。
2. 若 count 大於 1，只將 count 與總大小各減少 1。
3. 若 count 為 1，總大小減少 1，並依子節點數移除節點：
   - 無子節點：返回空子樹。
   - 只有一個子節點：以該子節點取代目標。
   - 有兩個子節點：找到右子樹中最小的節點（中序後繼），將其元素與**完整 count** 複製到目標，再從右子樹移除後繼節點。

`findMin` 沿左子節點尋找最小值；`deleteMin` 移除該節點並接回其右子樹。`deleteMin` 不再調整總大小，因為後繼節點的所有出現次數已搬到目標位置，整個操作只刪除原目標的一次出現。

### 走訪：iterator

iterator 使用 `LinkedStack` 模擬中序走訪，回傳型別為 `E`，不是 `CountedElement<E>`。

1. 初始化時，將根到最左節點的路徑依序推入堆疊。
2. `hasNext()` 檢查是否仍有尚未回傳的重複元素，或堆疊中仍有節點。
3. `next()` 若還有重複次數，直接回傳目前元素並減少待回傳次數。
4. 否則彈出下一個節點，記錄其剩餘次數為 count − 1，將右子樹的最左路徑推入堆疊，再回傳該元素。
5. 沒有下一個元素時，`next()` 拋出 `NoSuchElementException`。

因此元素依自然順序輸出，且每個元素會回傳 count 次。使用期間不應修改 Bag；目前 iterator 沒有並行修改偵測。

### 其他操作

- `contains`：依 BST 比較結果搜尋元素。
- `isEmpty`、`size`：查詢目前總大小；`clear`：清空根節點並將大小設為零。
- `equals(Bag<E>)`：先比較總大小，再使用 `HashMap` 累計本 Bag 的元素次數，走訪另一個 Bag 逐次扣除，確認元素與次數一致。這是 Bag 介面的方法，並非覆寫 `Object.equals(Object)`。

`add(null)` 與 `remove(null)` 會忽略輸入；`contains(null)` 在非空樹上可能拋出 `NullPointerException`。一般使用請傳入非 null 元素，並保持 `compareTo`、`equals` 與 `hashCode` 的相等語意一致。

### 複雜度

令 `n` 為總出現次數、`u` 為不同元素數、`h` 為樹高。以下將單次比較及雜湊視為常數時間。

| 操作 | 時間複雜度 | 額外空間 |
| --- | --- | --- |
| Part A 處理全部輸入 | 最壞 O(nu)，重複字詞會線性搜尋計數集合 | O(u) |
| `BSTBag.add`、`contains` | O(h) | O(1) |
| `BSTBag.remove` | O(h) | O(h)，遞迴呼叫堆疊 |
| `isEmpty`、`size`、`clear` | O(1)，不計垃圾回收 | O(1) |
| iterator 初始化 | O(h) | O(h) |
| `hasNext` | O(1) | O(1) |
| `next` | 單次最壞 O(h)；完整走訪攤銷每次 O(1) | 與 iterator 共用 O(h) 堆疊 |
| 完整走訪 | O(n) | O(h) |
| `equals`（兩個同大小的 BSTBag） | 預期 O(n)，假設雜湊操作平均 O(1) | O(u) |

BST 本身儲存 O(u) 個節點。樹形良好時 `h` 為 O(log u)，但此實作不自動平衡，最壞可退化至 O(u)，因此新增、搜尋與刪除不保證 O(log u)。


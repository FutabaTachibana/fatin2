# Git 提交记录回退指南

## 问题描述
当前分支存在重复的提交信息，需要将提交记录回退到 "upgrade Java version to 21" 这次提交。

## 当前状态分析

### 提交历史
```
b512a54 (HEAD) Initial plan
ebf3c11 Merge remote-tracking branch 'origin/dev' into dev
5b901c8 feat: upgrade Java version to 21 and refactor message handling with new Message class
... (更早的提交)
```

### 重复提交分析
在合并操作 (ebf3c11) 中，引入了重复的提交记录。目标是回退到提交 5b901c8。

## 解决方案

### 方案一：使用 git reset（推荐，但需要 force push）

如果你有权限进行 force push，这是最干净的方法：

```bash
# 1. 确认目标提交的 SHA
git log --oneline -10

# 2. 重置到目标提交（保留工作区的修改）
git reset --soft 5b901c8

# 或者，重置到目标提交（丢弃所有修改）
git reset --hard 5b901c8

# 3. 查看状态
git status

# 4. 如果使用了 --soft，你可以重新提交
git commit -m "Revert to upgrade Java version to 21"

# 5. 强制推送到远程分支
git push --force origin <branch-name>
```

**注意**: `--force` 会覆盖远程分支历史，如果其他人也在使用这个分支，需要提前通知他们。

### 方案二：使用 git revert（不需要 force push）

如果不能使用 force push，可以使用 revert 来撤销不需要的提交：

```bash
# 1. 撤销从 5b901c8 之后到 HEAD 的所有提交
git revert --no-commit 5b901c8..HEAD

# 2. 提交撤销
git commit -m "Revert commits after upgrade Java version to 21"

# 3. 推送到远程
git push origin <branch-name>
```

这种方法会创建新的撤销提交，保留完整的历史记录。

### 方案三：创建新分支（最安全）

如果想保留原分支作为备份：

```bash
# 1. 创建新分支指向目标提交
git checkout -b clean-branch 5b901c8

# 2. 推送新分支到远程
git push origin clean-branch

# 3. 如果需要，可以删除旧分支
git branch -d <old-branch-name>
git push origin --delete <old-branch-name>
```

## 具体操作步骤

### 推荐操作（针对当前情况）

基于当前分析，建议执行：

```bash
# 1. 确保在正确的分支上
git checkout dev  # 或你的目标分支

# 2. 重置到 5b901c8
git reset --hard 5b901c8

# 3. 强制推送（需要权限）
git push --force origin dev
```

### 如果不能使用 force push

```bash
# 1. 创建一个新的提交来撤销更改
git revert --no-commit 5b901c8..HEAD
git commit -m "Revert to upgrade Java version to 21"

# 2. 正常推送
git push origin dev
```

## 验证

完成操作后，验证提交历史：

```bash
# 查看提交历史
git log --oneline -10

# 查看当前状态
git status
```

## 注意事项

1. **备份**: 在执行任何重置操作前，建议先备份当前分支：
   ```bash
   git branch backup-before-reset
   ```

2. **团队协作**: 如果其他人也在使用这个分支，使用 force push 前务必通知他们。

3. **已推送的提交**: 如果要撤销的提交已经被其他人拉取，建议使用 `git revert` 而不是 `git reset`。

4. **检查差异**: 在重置前，可以先检查文件差异：
   ```bash
   git diff 5b901c8..HEAD
   ```

## 目标提交信息

- **提交 SHA**: 5b901c8
- **提交信息**: feat: upgrade Java version to 21 and refactor message handling with new Message class
- **作者**: Futaba_Tachibana
- **日期**: Wed Dec 17 15:27:52 2025 +0800

---

如果需要进一步帮助，请查阅 [Git 官方文档](https://git-scm.com/docs) 或咨询团队的 Git 管理员。

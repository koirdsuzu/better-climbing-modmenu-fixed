# Better Climbing

Small multiloader mod that removes the slowdown from moving in climbable blocks,
lets you drop down climbable blocks faster by looking down, and lets you jump
from the ground while standing in climbable blocks.

# Better Climbing (Mod Menu) - Minecraft 1.21

Better Climbing を Minecraft 1.21 / Fabric 向けに再構成した設定可能版です。

Mod Menu の設定画面から以下を個別に ON/OFF できます。

- 登攀中の横移動改善
- 高速降下
- 高速登攀
- 登攀中のジャンプ
- 意図しない衝突を無視

## ビルド

必要:
- Java 21
- Gradle 8.12.1 以上
- インターネット接続

```powershell
gradle wrapper --gradle-version 8.12.1
.\gradlew.bat build
```

完成した jar は `build/libs/` に生成されます。

## 重要

このプロジェクトは元の Better Climbing 1.21 系コードが使用している **Mojang mappings** に合わせています。Yarn mappings にはしていません。

Mod Menu は 11.0.5 をビルド時 API として使用します。

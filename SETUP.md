# TrainingLog Android アプリ セットアップ手順

---

## 1. 事前準備（インストール）

### Android Studio
https://developer.android.com/studio からダウンロードしてインストール

### JDK（Android Studioに同梱されているので通常は不要）
Android Studio Hedgehog以降は内蔵JDK 17が使われます。

---

## 2. プロジェクトをAndroid Studioで開く

1. Android Studioを起動
2. 「Open」→ この `TrainingLog` フォルダを選択
3. Gradle syncが自動で始まるので完了まで待つ（初回は数分かかる）

---

## 3. アイコン画像を設定する（任意）

`app/src/main/res/mipmap-hdpi/` に以下の画像を置く：
- `ic_launcher.png`（72×72px）
- `ic_launcher_round.png`（72×72px）

置かない場合はAndroid Studioで
「右クリック → New → Image Asset」でデフォルトアイコンを自動生成できます。

---

## 4. 実機またはエミュレータで実行

### 実機の場合
1. Androidの「設定 → 開発者オプション → USBデバッグ」をONにする
2. PCとUSBで接続
3. Android Studioの ▶ ボタンで実行

### エミュレータの場合
1. Android Studio → Device Manager → 「Create Device」
2. Pixel 7などを選択、API 26以上のSystemImageをダウンロード
3. ▶ ボタンで実行

---

## 5. APKをビルドして端末に直接インストール

1. メニュー → Build → Build Bundle(s)/APK(s) → Build APK(s)
2. `app/build/outputs/apk/debug/app-debug.apk` が生成される
3. このファイルをAndroid端末に転送してインストール
   （設定 → セキュリティ → 提供元不明のアプリを許可）

---

## 6. データの引き継ぎ（ブラウザ → アプリ）

ブラウザ版でlocalStorageに保存したデータはアプリのWebViewとは別です。
引き継ぐには：

1. ブラウザ版でコンソールを開く（開発者ツール）
2. 以下を実行してデータをコピー：
   ```
   copy(localStorage.getItem('training_records'))
   copy(localStorage.getItem('water_data'))
   ```
3. アプリ版でコンソールから以下を実行：
   ```
   localStorage.setItem('training_records', '<コピーしたデータ>')
   ```

または、import_data.html を assets フォルダに追加して
アプリ内からインポートする方法も使えます。

---

## フォルダ構成

```
TrainingLog/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── index.html        ← アプリの本体HTML
│   │   ├── java/com/traininglog/app/
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── mipmap-hdpi/      ← アイコン画像を置く
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## HTMLを更新したい場合

`app/src/main/assets/index.html` を差し替えてビルドし直すだけでOKです。

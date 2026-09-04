# nothing-camera

Nothing Phone (2a) 向けの自作カメラアプリです。

## なぜ作るのか

プリインストールされているカメラアプリの色味が気に入らないため、自分でアプリを作ることにしました。

- 肌の色が不自然
- 彩度・コントラストが過剰

Nothing のカメラアプリは独自の後処理で派手な絵作りをしているため、サードパーティアプリで
素のカメラパイプラインから撮影することで、見たままに近いナチュラルな発色を目指します。

## 対象端末

Nothing Phone (2a) のみを対象とした、作者の私用端末向けの個人プロジェクトです。

## 機能

- 静止画（JPEG）撮影
- 動画撮影

## 技術スタック

- Kotlin
- Jetpack Compose
- CameraX（Preview / ImageCapture / VideoCapture）
- 色味の微調整は Camera2Interop 経由で `CaptureRequest` のキーを制御して行います

## ビルド

Android Studio でプロジェクトを開き、実機にインストールしてください。

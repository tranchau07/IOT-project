# 📖 PlantUML - Hướng Dẫn Cài Đặt & Sử Dụng Chi Tiết

## 🎯 Mục tiêu
Hướng dẫn từng bước cài đặt PlantUML extension và sử dụng để view + export diagrams.

---

## 📦 BƯỚC 1: Cài Đặt Extension PlantUML

### 1.1 Mở VS Code Extensions
- Nhấn tổ hợp phím: **`Ctrl + Shift + X`** (Windows/Linux) hoặc **`Cmd + Shift + X`** (Mac)
- Hoặc nhấn vào biểu tượng **Extensions** ở thanh sidebar bên trái

![VS Code Extensions Panel]
```
┌─────────────────────────────────────────┐
│ VS CODE WINDOW                          │
├──────────┬──────────────────────────────┤
│ SIDEBAR  │                              │
│  📁      │    EXTENSIONS PANEL          │
│  🔍      │                              │
│  🔗      │  [Search box]                │
│  ▶️      │  ┌────────────────────────┐  │
│  🐛      │  │ Installed (5)          │  │
│  🧪      │  ├────────────────────────┤  │
│  📋      │  │ Extension A            │  │
│          │  │ Extension B            │  │
│          │  │ Extension C            │  │
│          │  └────────────────────────┘  │
└──────────┴──────────────────────────────┘
```

### 1.2 Tìm Kiếm Extension
Trong **Search Extensions** box:
1. Gõ: `PlantUML`
2. Tìm extension có tên **"PlantUML"**
3. Tác giả: **jebbs** (ID: `jebbs.plantuml`)

```
Search Box: [PlantUML_______________]  ×

RESULTS:
────────────────────────────────────────
PlantUML                          ⭐⭐⭐⭐⭐
ID: jebbs.plantuml
Description: Rich PlantUML support for VS Code
Download: 2.5M↓  Rating: 4.8/5
🟢 Status: Latest version

Maintainer: jebbs
───────────────────────────────────────
```

### 1.3 Cài Đặt
1. Nhấn vào result để mở trang chi tiết
2. Nhấn nút **"Install"** (button xanh)
3. Chờ cài đặt hoàn tất (30 giây - 2 phút tùy tốc độ internet)
4. Sau khi xong, button "Install" sẽ thay đổi thành "Uninstall"

```
PlantUML by jebbs
────────────────────────────────────────
Version: 2.18.0

[Install]  or  [Uninstall] (nếu đã cài)

Description:
Provides syntax highlight and snippets support for PlantUML...
```

### 1.4 Kiểm Tra Cài Đặt
Nhấn **`Ctrl + Shift + P`** để mở Command Palette, gõ:
```
PlantUML: Preview
```

Nếu lệnh này hiện lên → **Cài đặt thành công! ✅**

---

## 🎬 BƯỚC 2: Mở File .puml và Preview

### 2.1 Mở File .puml
Có 3 cách:

#### **Cách 1: Từ File Explorer**
1. Mở folder dự án: `Iot-Project/diagrams`
2. Nhấn vào file `.puml` (ví dụ: `sequence_sensor_data_publish.puml`)
3. File sẽ hiện trong editor

#### **Cách 2: Từ Command Line**
```bash
# Windows PowerShell hoặc CMD
cd "c:\Users\TRAN MINH CHAU\...\Iot-Project\diagrams"
code sequence_sensor_data_publish.puml
```

#### **Cách 3: Từ Command Palette**
1. Nhấn **`Ctrl + Shift + P`**
2. Gõ: `File: Open File`
3. Chọn file `.puml`

### 2.2 Preview Diagram (3 cách)

#### **Cách 1: Keyboard Shortcut (Nhanh nhất!)**
1. Mở file `.puml`
2. Nhấn **`Alt + D`** (Windows/Linux) hoặc **`Option + D`** (Mac)
3. Preview panel sẽ hiện **bên cạnh** file editor

```
┌──────────────────┬──────────────────┐
│  sequence_       │                  │
│  sensor_data_    │   PREVIEW PANEL  │
│  publish.puml    │   ┌────────────┐ │
│                  │   │ Diagram    │ │
│ @startuml        │   │ rendering  │ │
│ title Sensor...  │   │            │ │
│ participant ...  │   │    [IMG]   │ │
│ Device -> Broker │   │            │ │
│ ...              │   │            │ │
│                  │   └────────────┘ │
└──────────────────┴──────────────────┘
```

#### **Cách 2: Từ Command Palette**
1. Nhấn **`Ctrl + Shift + P`**
2. Gõ: `PlantUML: Preview Current File`
3. Enter → Preview hiện ra

#### **Cách 3: Right-click Context Menu**
1. Nhấn chuột phải vào file `.puml`
2. Chọn: **"PlantUML: Preview Current File"**
3. Preview hiện ra

### 2.3 Điều Khiển Preview Panel

| Action | Cách làm |
|--------|----------|
| 🔍 **Zoom In** | Scroll lên hoặc `Ctrl +` |
| 🔍 **Zoom Out** | Scroll xuống hoặc `Ctrl -` |
| 🏠 **Fit to Window** | `Ctrl + Shift + H` hoặc Double-click |
| 📋 **Copy Image** | Right-click → "Copy" |
| 💾 **Save as File** | Right-click → "Save" |
| ❌ **Close Preview** | Click X button hoặc `Alt + D` again |

---

## 💾 BƯỚC 3: Export Diagram (PNG/SVG/PDF)

### 3.1 Method 1: Command Palette (Recommended)

#### **Export PNG** (Phổ biến nhất)
1. Mở file `.puml`
2. Nhấn **`Ctrl + Shift + P`**
3. Gõ: `PlantUML: Export Current File as png`
4. Chọn kết quả → Enter
5. **Chọn folder** nơi lưu file
6. Chờ 2-5 giây

✅ File `.png` sẽ được lưu cùng folder với `.puml`

#### **Export SVG** (Vector, có thể phóng to không bị mờ)
1. Nhấn **`Ctrl + Shift + P`**
2. Gõ: `PlantUML: Export Current File as svg`
3. Chọn → Enter
4. **Chọn folder** → File `.svg` được lưu

#### **Export PDF** (Để in hoặc gửi)
1. Nhấn **`Ctrl + Shift + P`**
2. Gõ: `PlantUML: Export Current File as pdf`
3. Chọn → Enter
4. **Chọn folder** → File `.pdf` được lưu

### 3.2 Method 2: Right-Click Context Menu

1. Nhấn **chuột phải** vào file `.puml` trong editor
2. Menu hiện ra:
   ```
   PlantUML: Preview Current File
   PlantUML: Export Current File as png     ← Nhấn đây
   PlantUML: Export Current File as svg
   PlantUML: Export Current File as pdf
   ```
3. Chọn format muốn export
4. Chọn folder lưu → File được tạo

### 3.3 Method 3: Keyboard Shortcut (Nếu cấu hình)

**Cấu hình Custom Shortcut:**
1. Nhấn **`Ctrl + K Ctrl + S`** (hoặc **`Cmd + K Cmd + S`** trên Mac)
2. Tìm: `plantuml`
3. Tìm lệnh: `PlantUML: Export Current File as png`
4. Nhấn vào dòng đó → nhập shortcut muốn (ví dụ: `Alt + E`)
5. Lần sau chỉ cần nhấn **`Alt + E`** → export ngay!

---

## 🔧 BƯỚC 4: Cấu Hình Nâng Cao (Optional)

### 4.1 Tự động Export vào Folder Cụ Thể

Mở file `.vscode/settings.json` (hoặc tạo nếu chưa có):

```bash
# Tạo folder .vscode nếu chưa có
mkdir .vscode

# Mở/tạo file settings.json
code .vscode/settings.json
```

**Thêm cấu hình:**
```json
{
  "plantuml.exportOutDir": "./diagrams/exports",
  "plantuml.exportFormat": "png",
  "plantuml.render": "Local",
  "plantuml.commandArgs": ["-DPLANTUML_LIMIT_SIZE=8192"],
  "plantuml.server": "",
  "plantuml.diagramsRoot": "./diagrams"
}
```

**Giải thích:**
| Setting | Ý nghĩa |
|---------|---------|
| `exportOutDir` | Folder lưu export (tự động tạo) |
| `exportFormat` | Format mặc định: png / svg / pdf |
| `render` | Local = xử lý trên máy (nhanh hơn) |
| `commandArgs` | Tăng kích thước giới hạn |
| `diagramsRoot` | Folder chứa diagrams |

### 4.2 Cài Đặt Java (Nếu cần tốc độ cao)

PlantUML sử dụng **Graphviz** để render. Cài đặt Graphviz:

**Windows:**
```bash
# Dùng Chocolatey
choco install graphviz

# Hoặc download từ: https://graphviz.org/download/
```

**macOS:**
```bash
brew install graphviz
```

**Linux:**
```bash
sudo apt-get install graphviz
```

---

## 📚 BƯỚC 5: Ví Dụ Thực Tế - Export Tất Cả Diagrams

### 5.1 Export Từng File Một

```
Folder: Iot-Project/diagrams/

File 1: sequence_sensor_data_publish.puml
  ↓ Alt + D → Preview
  ↓ Ctrl+Shift+P → Export PNG
  ✅ Output: sequence_sensor_data_publish.png

File 2: sequence_user_command_flow.puml
  ↓ Alt + D → Preview
  ↓ Ctrl+Shift+P → Export PNG
  ✅ Output: sequence_user_command_flow.png

... (tiếp tục với các file khác)
```

### 5.2 Export Batch (Tất cả cùng lúc)

**Sử dụng PowerShell Script:**

Tạo file `export-all.ps1`:
```powershell
# export-all.ps1
cd "c:\Users\TRAN MINH CHAU\OneDrive - Hanoi University of Science and Technology\Documents\spring boot project\Iot-Project\diagrams"

$pumlFiles = Get-ChildItem -Filter "*.puml"

foreach ($file in $pumlFiles) {
    Write-Host "Exporting $($file.Name) to PNG..."
    # Gọi VS Code PlantUML export
    code --command "plantUmlExportCurrent png" $file.FullName
    Start-Sleep -Seconds 2
}

Write-Host "Done! All diagrams exported."
```

Chạy:
```bash
.\export-all.ps1
```

---

## ✅ Kiểm Tra & Troubleshooting

### ✅ Cài Đặt Thành Công?

```
✅ PlantUML extension hiện trong "Installed Extensions"
✅ Alt + D hoạt động → preview hiện ra
✅ Ctrl+Shift+P gõ "PlantUML" → có kết quả
✅ Export PNG/SVG/PDF thành công
```

### ❌ Lỗi: "PlantUML: Preview not found"

**Nguyên nhân:** Extension chưa cài đặt

**Giải pháp:**
1. Mở Extensions: `Ctrl + Shift + X`
2. Tìm "PlantUML" → Install
3. Reload VS Code: `Ctrl + Shift + P` → "Reload Window"

### ❌ Lỗi: "Preview không hiện"

**Nguyên nhân:** File `.puml` bị lỗi cú pháp

**Giải pháp:**
1. Kiểm tra file có `@startuml` và `@enduml` không
2. Kiểm tra syntax lỗi (Output panel sẽ hiện lỗi)
3. Xem trực tuyến: http://www.plantuml.com/plantuml/uml/

### ❌ Lỗi: "Export failed"

**Nguyên nhân:** Graphviz chưa cài

**Giải pháp:**
```bash
# Windows
choco install graphviz

# macOS
brew install graphviz

# Linux
sudo apt-get install graphviz
```

---

## 🎯 Quy Trình Nhanh (Cheat Sheet)

### Mỗi khi sửa diagram:
```
1. Mở file .puml
   ↓
2. Edit content
   ↓
3. Alt + D → Preview (tự động reload)
   ↓
4. Ctrl+Shift+P → "Export Current File as png"
   ↓
5. ✅ File .png được lưu
```

### Export multiple diagrams:
```
Ctrl+Shift+P → "PlantUML: Export Workspace Diagrams"
↓
Chọn format
↓
Chọn folder
↓
✅ Tất cả .puml export thành .png/.svg
```

---

## 📊 Danh Sách Files & Output

**Input Files (Diagrams):**
```
diagrams/
├── sequence_sensor_data_publish.puml      → ❌ PNG
├── sequence_user_command_flow.puml        → ❌ PNG
├── sequence_device_bootstrap.puml         → ❌ PNG
├── sequence_authentication_flow.puml      → ❌ PNG
├── sequence_scheduled_job.puml            → ❌ PNG
├── activity_device_lifecycle.puml         → ❌ PNG
├── activity_backend_mqtt_pipeline.puml    → ❌ PNG
├── activity_app_user_flow.puml            → ❌ PNG
├── activity_command_lifecycle.puml        → ❌ PNG
└── overview_system_architecture.puml      → ❌ PNG
```

**Output Folder (sau export):**
```
diagrams/
├── exports/  (nếu cấu hình exportOutDir)
│   ├── sequence_sensor_data_publish.png
│   ├── sequence_user_command_flow.png
│   ├── ... (tất cả diagrams)
│   └── overview_system_architecture.png
```

---

## 🎓 Tips & Tricks

### 1. **Live Editing**
- Mở split view: Click file → Nhấn `Ctrl + \`
- Bên trái: editor, bên phải: preview
- Sửa code, preview tự động update

### 2. **Copy Diagram**
- Preview panel → Right-click → Copy
- Paste vào document/email/wiki

### 3. **Full Screen Preview**
- Preview panel → Click biểu tượng "Expand"
- Xem full screen diagram

### 4. **Export Multiple Formats**
- Export PNG rồi export SVG → có cả 2 file
- SVG để web (có thể zoom), PNG để in

### 5. **Online Share**
- Copy nội dung `.puml`
- Paste vào http://www.plantuml.com/plantuml/uml/
- Share link với team

---

## 📞 Hỗ Trợ & Tài Liệu

| Nhu cầu | Link |
|--------|------|
| Extension Home | https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml |
| PlantUML Official | http://plantuml.com |
| Syntax Help | http://plantuml.com/guide |
| Online Editor | http://www.plantuml.com/plantuml/uml/ |
| GitHub Issues | https://github.com/plantuml-stdlib/C4-PlantUML |

---

## ✨ Tổng Kết

| Step | Action | Shortcut | Kết quả |
|------|--------|----------|---------|
| 1 | Cài extension | `Ctrl+Shift+X` → PlantUML | ✅ Cài xong |
| 2 | Mở file .puml | `Ctrl+O` | 📄 File open |
| 3 | Preview | `Alt+D` | 👀 Xem diagram |
| 4 | Edit | Normal editing | ✏️ Sửa code |
| 5 | Export | `Ctrl+Shift+P` → Export | 💾 .png/.svg/.pdf |

**🎉 Hoàn tất! Sẵn sàng dùng PlantUML.**

---

**Last Updated:** January 30, 2026
**For:** IoT Project Team

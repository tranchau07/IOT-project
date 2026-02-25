# 🎯 PlantUML Diagrams - Quick Reference & Export Commands

## 📋 Danh sách Diagrams

### Sequence Diagrams (5 files)
| # | File | Mô tả | Lifelines |
|---|------|-------|-----------|
| 1 | `sequence_sensor_data_publish.puml` | Sensor data flow từ device → backend → app | 6 actors |
| 2 | `sequence_user_command_flow.puml` | User control command flow | 7 actors |
| 3 | `sequence_device_bootstrap.puml` | Device boot & registration | 6 actors |
| 4 | `sequence_authentication_flow.puml` | App login & JWT handling | 6 actors |
| 5 | `sequence_scheduled_job.puml` | Backend scheduled status publish | 5 actors |

### Activity Diagrams (4 files)
| # | File | Mô tả | States |
|---|------|-------|--------|
| 1 | `activity_device_lifecycle.puml` | ESP32 internal loop & lifecycle | Boot, WiFi, NTP, MQTT, Main loop |
| 2 | `activity_backend_mqtt_pipeline.puml` | Backend MQTT message processing | Receive, Parse, Validate, Route, Process |
| 3 | `activity_app_user_flow.puml` | App login & device control flow | Auth, Main, Device detail, Control |
| 4 | `activity_command_lifecycle.puml` | Command state transitions | PENDING → SENT → ACKED → COMPLETED/FAILED |

### Overview Diagram (1 file)
| # | File | Mô tả |
|---|------|-------|
| 1 | `overview_system_architecture.puml` | System architecture overview |

---

## 🚀 Export Commands

### Yêu cầu
```bash
# Cài đặt Java (if not already installed)
# Download PlantUML JAR: http://plantuml.com/download

# Hoặc sử dụng package manager:
# macOS
brew install plantuml

# Windows (Chocolatey)
choco install plantuml

# Linux
apt-get install plantuml
```

### Export từ Command Line

#### Export tất cả diagrams (PNG)
```bash
# Windows PowerShell
cd "c:\Users\TRAN MINH CHAU\OneDrive - Hanoi University of Science and Technology\Documents\spring boot project\Iot-Project\diagrams"

# Export PNG (default)
plantuml.exe -tpng sequence_*.puml
plantuml.exe -tpng activity_*.puml
plantuml.exe -tpng overview_*.puml

# Hoặc từng file
plantuml.exe -tpng sequence_sensor_data_publish.puml
```

#### Export sang SVG (Vector format)
```bash
plantuml.exe -tsvg sequence_sensor_data_publish.puml
```

#### Export sang PDF
```bash
plantuml.exe -tpdf sequence_sensor_data_publish.puml
```

#### Export tất cả định dạng
```bash
@echo off
for %%f in (*.puml) do (
    echo Exporting %%f...
    plantuml.exe -tpng %%f
    plantuml.exe -tsvg %%f
    plantuml.exe -tpdf %%f
)
echo Done!
```

### Sử dụng Docker
```bash
docker run --rm -v /path/to/diagrams:/data plantuml/plantuml:latest -tpng /data/*.puml
```

### Visual Studio Code Setup

#### 1. Cài đặt Extension
- Mở VS Code Extensions (Ctrl+Shift+X)
- Tìm "PlantUML"
- Cài đặt extension `jebbs.plantuml`

#### 2. Cấu hình (`.vscode/settings.json`)
```json
{
    "plantuml.exportOutDir": "./diagrams/exports",
    "plantuml.exportFormat": "png",
    "plantuml.render": "Local"
}
```

#### 3. Shortcuts
| Action | Shortcut |
|--------|----------|
| Preview | `Alt+D` |
| Export | `Ctrl+Shift+P` → "PlantUML: Export" |
| Export As PNG | `Ctrl+Shift+P` → "PlantUML: Export Current File as PNG" |

#### 4. Right-click Menu
- Mở file `.puml` → Right-click → "PlantUML: Preview Current File"

---

## 📊 Diagram Specifications

### Sequence Diagrams
- **Best for:** Time-based interactions, message flows, synchronous/asynchronous patterns
- **Viewer:** Online (plantuml.com), VS Code, IDE plugins
- **Size:** Medium (typical 800-1200px width)

### Activity Diagrams
- **Best for:** Process flows, decision trees, state transitions
- **Viewer:** Online (plantuml.com), VS Code, IDE plugins
- **Size:** Medium-Large (depends on complexity)

---

## 🎨 Customization Tips

### Thay đổi màu sắc
```plantuml
skinparam sequenceArrowColor Blue
skinparam actorBackgroundColor #FFFFCC
skinparam backgroundColor #FEFEFE
```

### Thay đổi font
```plantuml
skinparam defaultFontName Arial
skinparam defaultFontSize 12
```

### Thay đổi kích thước trang
```plantuml
scale 1.5
' hoặc
scale 80%
```

### Zoom & Pan
- Các diagram hỗ trợ zoom khi export SVG
- PNG có fixed size

---

## 🔍 Troubleshooting

### PlantUML not found
```bash
# Cài đặt lại
npm install -g @plantuml/cli

# Hoặc download JAR manual
# http://plantuml.com/download
```

### Output format not supported
```bash
# Kiểm tra các format được hỗ trợ
plantuml -help

# Đảm bảo Java đã cài đặt
java -version
```

### Diagram too large
```plantuml
' Giảm scale
scale 0.8

' Hoặc tăng khoảng cách
skinparam ranksep 30
skinparam nodesep 20
```

### Special characters not displaying
- Lưu file dạng UTF-8
- Thêm header: `' -*- coding: utf-8 -*-`

---

## 📚 Tài liệu tham khảo

### PlantUML Documentation
- Official: http://plantuml.com
- Sequence Diagram: http://plantuml.com/sequence-diagram
- Activity Diagram: http://plantuml.com/activity-diagram-beta
- Real-time Renderer: http://www.plantuml.com/plantuml/uml/

### Online Editors
- PlantUML Editor: http://www.plantuml.com/plantuml/uml/
- Alternative: https://www.planttext.com/
- VS Code: jebbs.plantuml extension

### Integration
- GitHub: PlantUML files auto-render in README.md
- Confluence: PlantUML plugin available
- Jira: Draw.io with PlantUML support
- Gitlab: Native PlantUML support

---

## ✅ Best Practices

### Naming
- ✅ Sử dụng tên mô tả: `sequence_sensor_data_publish.puml`
- ❌ Tránh tên chung: `diagram1.puml`

### Organization
- ✅ Tách sequence & activity diagrams
- ✅ Dùng prefix cho dễ tìm
- ❌ Lẫn lộn các loại diagram

### Documentation
- ✅ Thêm title rõ ràng
- ✅ Thêm notes giải thích
- ✅ Describe lifelines/actors
- ❌ Diagram quá phức tạp

### Maintenance
- ✅ Update khi architecture thay đổi
- ✅ Version control (git)
- ✅ Review cùng code review
- ❌ Để diagram lỗi thời

---

## 🎁 Bonus: Batch Export Script

### PowerShell (Windows)
Lưu file `export-diagrams.ps1`:
```powershell
param(
    [string]$DiagramPath = "./diagrams",
    [string[]]$Formats = @("png", "svg", "pdf")
)

Get-ChildItem $DiagramPath -Filter "*.puml" | ForEach-Object {
    Write-Host "Processing $($_.Name)..."
    $name = $_.BaseName
    
    foreach ($format in $Formats) {
        Write-Host "  → Exporting to $format..."
        & plantuml.exe -t$format $_.FullName
    }
}

Write-Host "Export complete!"
```

Chạy:
```bash
.\export-diagrams.ps1 -DiagramPath ".\diagrams" -Formats @("png", "svg")
```

### Bash (macOS/Linux)
Lưu file `export-diagrams.sh`:
```bash
#!/bin/bash

DIAGRAM_DIR="${1:-.}"
FORMATS="${2:-png}"

for puml_file in "$DIAGRAM_DIR"/*.puml; do
    echo "Processing $(basename $puml_file)..."
    for format in $FORMATS; do
        echo "  → Exporting to $format..."
        plantuml -t$format "$puml_file"
    done
done

echo "Export complete!"
```

Chạy:
```bash
chmod +x export-diagrams.sh
./export-diagrams.sh ./diagrams "png svg pdf"
```

---

**Created:** January 30, 2026
**Last Updated:** January 30, 2026
**PlantUML Version:** 1.2024.0+

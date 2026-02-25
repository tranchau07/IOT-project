# IoT Project - PlantUML Diagrams

Bộ sưu tập các sơ đồ PlantUML mô tả kiến trúc và quy trình của hệ thống IoT, bao gồm cả giao tiếp giữa ESP32, MQTT Broker, Spring Boot Backend, MongoDB/MySQL, WebSocket, và React Native App.

## 📊 Sequence Diagrams

### 1. **Sensor Data Publishing Flow** (`sequence_sensor_data_publish.puml`)
**Luồng: Device → Backend Pipeline → App**

Mô tả quy trình chuyên sâu:
- ✅ ESP32 xuất bản dữ liệu cảm biến mỗi 5 giây
- ✅ MQTT Broker nhận và chuyển tiếp tới Backend
- ✅ Backend xác thực schema và lưu vào MongoDB
- ✅ Backend gửi update qua WebSocket
- ✅ React Native App nhận update real-time
- ⚠️ Xử lý lỗi kết nối Broker (reconnect exponential backoff)
- 🔄 Loop asynchronous publish

**Lifelines:**
- Device (ESP32)
- MQTT Broker
- Spring Boot Backend (MQTT Inbound Handler)
- MongoDB
- WebSocket
- React Native App

---

### 2. **User Command Flow** (`sequence_user_command_flow.puml`)
**Luồng: App → Backend → Device → Response → App**

Mô tả request-response cycle:
- ✅ User bấm nút điều khiển (ON/OFF LED)
- ✅ App gửi POST /api/devices/{id}/on
- ✅ Backend publish MQTT tới Device
- ✅ Device thực thi lệnh và gửi response
- ✅ Backend xử lý response và cập nhật DB
- ✅ App nhận notification qua WebSocket
- 🔄 Thể hiện cả request-response + callback response

**Lifelines:**
- React Native App UI
- REST API (DeviceController)
- Backend (MQTT Outbound)
- MQTT Broker
- ESP32 Device
- Backend (Response Handler)
- WebSocket

---

### 3. **Device Bootstrap & Registration** (`sequence_device_bootstrap.puml`)
**Luồng: Device Boot → Register → Backend**

Mô tả quy trình khởi động thiết bị:
- ✅ Boot hardware
- ✅ Kết nối WiFi (retry mechanism)
- ✅ NTP Sync (UTC time)
- ✅ Kết nối MQTT Broker
- ✅ Subscribe topics: control, status
- ✅ Publish registration message
- ✅ Backend lưu device metadata vào MySQL
- ⚠️ Xử lý retry nếu kết nối WiFi/MQTT fail

**Lifelines:**
- ESP32 Device
- WiFi Network
- NTP Server
- MQTT Broker
- Spring Boot Backend
- MySQL (Device Registry)

---

### 4. **Authentication Flow** (`sequence_authentication_flow.puml`)
**Luồng: App Login → Backend → JWT Token**

Mô tả quy trình xác thực:
- ✅ User nhập username/password
- ✅ App gửi POST /identity/api/auth/login
- ✅ Backend xác thực credentials
- ✅ Backend tạo JWT token
- ✅ App lưu token vào AsyncStorage
- ✅ Token được đính kèm vào Authorization header cho API calls
- 🔄 Token refresh trước khi expire
- 📌 Token expiration & logout handling

**Lifelines:**
- React Native App
- AsyncStorage
- Auth Endpoint
- AuthController
- MySQL (Users Table)
- JWT Token Generator

---

### 5. **Scheduled Device Status Publishing** (`sequence_scheduled_job.puml`)
**Luồng: Backend Scheduled Task → Devices**

Mô tả quy trình check status định kỳ:
- 🕐 Spring Scheduler chạy mỗi 5 phút
- ✅ DeviceStatusPublisher fetch active devices
- ✅ Publish status check message
- ✅ Devices respond với status + metrics
- ✅ Backend cập nhật device metadata
- ⚠️ Đánh dấu device offline nếu không response

**Lifelines:**
- Spring Scheduler
- DeviceStatusPublisher
- Device Repository
- MQTT Broker
- ESP32 Devices

---

## 🔄 Activity Diagrams

### 1. **Device Internal Loop / Lifecycle** (`activity_device_lifecycle.puml`)
**Vòng lặp nội bộ ESP32**

Chi tiết các giai đoạn:
- 🔧 **Boot Phase:** Initialize hardware, load credentials
- 📡 **WiFi Connection:** Retry mechanism (max 20x, exponential backoff)
- 🕐 **NTP Sync:** UTC time synchronization
- 🔌 **MQTT Connection:** Retry with backoff
- 🔄 **Main Loop:**
  - Sensor Publishing Cycle (check isActive → publish if active → wait 5s)
  - Message Handler (parse JSON, validate command, execute action)
  - Reconnection Handler (detect lost connection, reconnect)

**Decision Points:**
- WiFi.status() == CONNECTED?
- MQTT connection failed?
- JSON parsing error?
- Topic == control?
- Status == ACTIVE|INACTIVE?
- MQTT connection lost?

---

### 2. **Backend MQTT Inbound Processing Pipeline** (`activity_backend_mqtt_pipeline.puml`)
**Pipeline xử lý tin nhắn MQTT đến**

Chi tiết:
- 📥 **Receive & Parse:** Extract headers, deserialize JSON
- ✅ **Validation:** Validate schema, deviceId, message type
- 🔀 **Routing:**
  - **Sensor Data:** Parse → Validate → Save MongoDB → Update MySQL → Push WebSocket
  - **Control Response:** Update command status → Notify API clients
  - **Device Status:** Parse status → Update device record
- ⚠️ **Error Handling:**
  - Malformed JSON → DLQ (Dead Letter Queue)
  - Unknown device → Log & discard
  - DB failure → Retry with exponential backoff

**Decision Points:**
- JSON valid?
- Device exists?
- Sensor data topic?
- Control response topic?
- Status topic?
- Schema valid?
- Insert successful?
- Update successful?
- Command found?

---

### 3. **App User Flow** (`activity_app_user_flow.puml`)
**Luồng: Login → View Devices → Control Device**

Các bước chính:
1. **Authentication Check:**
   - Check AsyncStorage token
   - Validate token if exists
   
2. **Login Flow (if not authenticated):**
   - Render LoginScreen
   - Validate input
   - Call POST /identity/api/auth/login
   - Save token to AsyncStorage
   
3. **Main App (if authenticated):**
   - Fetch device list (GET /api/devices)
   - Subscribe WebSocket for real-time updates
   - Display device list
   
4. **User Interactions:**
   - **View Device Details:** Show device info, sensors, control buttons
   - **Send Control Command:** Show PENDING state → Wait for response → Show SUCCESS/FAILED
   - **Pull to Refresh:** Fetch updated data
   - **Logout:** Clear token, reset state

**WebSocket & Polling:**
- Primary: WebSocket for real-time updates
- Fallback: REST polling if WebSocket fails

**Error Handling:**
- HTTP 401 → Token expired → Force re-login
- HTTP 400 → Validation error
- Network error → Show notification
- Command timeout → Show warning + allow retry

---

### 4. **Command Lifecycle** (`activity_command_lifecycle.puml`)
**Vòng đời lệnh: User Action → Final State**

Các trạng thái chính:

```
PENDING (tạo lệnh)
    ↓
SENT (publish MQTT)
    ↓
ACKED (device nhận)
    ↓
COMPLETED (device thực thi thành công)
    OR
FAILED (lỗi thực thi)
```

Chi tiết quy trình:
1. **Command Creation:** User triggers → Create command object → Call REST API
2. **Backend Queuing:** Validate → Create entity → Save to MySQL → Return 202
3. **MQTT Publishing:** Publish to device → Set status = SENT
4. **Device Execution:** Parse → Validate → Execute action → Create response
5. **Device Response:** Send MQTT response to backend
6. **Response Processing:** Parse response → Update command status → Send WebSocket
7. **App Notification:** Show SUCCESS/FAILED → Enable button → Update UI

**Timeout & Retry:**
- Default timeout: 10 seconds
- No automatic retry (user-initiated)
- Fall back to polling if WebSocket fails

---

## 🛠️ Cách sử dụng PlantUML

### Visual Studio Code
1. Cài đặt extension: **PlantUML** (jebbs.plantuml)
2. Mở file `.puml`
3. Nhấn `Ctrl+Alt+P` (hoặc `Cmd+Option+P` trên Mac) để preview
4. Nhấn `Ctrl+Shift+E` để export (PNG, SVG, PDF)

### Từ Command Line
```bash
# Cài đặt PlantUML
java -jar plantuml.jar sequence_sensor_data_publish.puml

# Export ra PNG
java -jar plantuml.jar -tpng sequence_sensor_data_publish.puml

# Export ra SVG
java -jar plantuml.jar -tsvg sequence_sensor_data_publish.puml
```

### Online Editor
Truy cập http://www.plantuml.com/plantuml/uml/ và paste nội dung file

---

## 📝 Ghi chú Kiến trúc

### MQTT Topics
```
devices/{deviceId}/sensors/{sensorId}/data        → Sensor data from device
devices/{deviceId}/control                         → Control commands to device
devices/{deviceId}/control/response                → Device response to commands
devices/{deviceId}/status                          → Device status check/update
```

### REST API Endpoints
```
POST   /identity/api/auth/login                   → Login
GET    /api/devices                                → List devices
POST   /api/devices/{id}/on                        → Control device ON
GET    /api/sensor-data/{sensorId}/latest         → Get latest sensor data
POST   /api/commands/{id}/status                   → Poll command status
```

### WebSocket Topics
```
/topic/devices/{deviceId}                          → Device updates
/topic/commands/{commandId}                        → Command responses
```

### Database
- **MongoDB:** sensorData, auditLog
- **MySQL:** users, devices, sensors, commands, rooms, roles

### Message Formats

**Sensor Data (Device → Backend)**
```json
{
  "sensorId": "693d1c8b...",
  "value": 23.5,
  "unit": "°C",
  "timestamp": "2024-01-30T10:30:45.000Z"
}
```

**Control Command (Backend → Device)**
```json
{
  "commandId": "550e8400-e29b-41d4-a716-446655440000",
  "action": "ON",
  "timestamp": "2024-01-30T10:30:45.000Z"
}
```

**Device Response (Device → Backend)**
```json
{
  "commandId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "SUCCESS",
  "respondedAt": "2024-01-30T10:30:46.000Z"
}
```

---

## 🔍 Debugging & Monitoring

### Các điểm cần monitor
- 📊 MQTT message throughput
- ⏱️ Command response time (target: < 10s)
- 🔗 WebSocket connection stability
- 🚨 Failed commands & error rates
- 💾 MongoDB & MySQL performance
- 📡 Device connection status & uptime

### Common Issues
| Issue | Cause | Solution |
|-------|-------|----------|
| Device not receiving commands | MQTT subscription failed | Check topic names & device ID |
| Slow command response | Network latency or device busy | Check MQTT broker load & device processing |
| WebSocket disconnect | Network instability | Implement reconnection retry |
| Invalid JSON from device | Firmware bug or memory issue | Validate payload on device |
| Missing sensor data | Device inactive or offline | Check device status & isActive flag |

---

**Last Updated:** January 30, 2026
**Project:** IoT System with Spring Boot + MQTT + React Native
**Author:** IoT Development Team

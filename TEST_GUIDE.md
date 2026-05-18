# Hướng dẫn chạy Unit Tests cho AutoControlEngineService

## Mô tả test file

File test `AutoControlEngineServiceTest.java` chứa **17 test cases** để kiểm tra các chức năng của `AutoControlEngineService`:

### 1. **Kiểm tra điều kiện Device (3 tests)**

| Test | Mô tả | Kỳ vọng |
|------|-------|--------|
| `testEvaluate_ClassroomNotFound()` | Classroom không tồn tại | Không gửi lệnh điều khiển |
| `testEvaluate_DeviceOffline()` | Device offline | Không gửi lệnh điều khiển |
| `testEvaluate_DevicePowerOff()` | Device tắt nguồn | Không gửi lệnh điều khiển |

---

### 2. **Kiểm tra xử lý phòng rỗng (4 tests)**

| Test | Mô tả | Kỳ vọng |
|------|-------|--------|
| `testHandleEmptyRoom_RoomEmptyFor10Minutes()` | Phòng rỗng ≥ 10 phút | **Gửi lệnh tắt thiết bị** |
| `testHandleEmptyRoom_RoomEmptyLessThan10Minutes()` | Phòng rỗng < 10 phút | Không gửi lệnh |
| `testHandleEmptyRoom_NoSensorData()` | Không có dữ liệu sensor | Không gửi lệnh |
| `testHandleEmptyRoom_RoomHasOccupants()` | Phòng vẫn có người | Không gửi lệnh |

---

### 3. **Kiểm tra xử lý lịch học (5 tests)**

| Test | Mô tả | Kỳ vọng |
|------|-------|--------|
| `testHandleSchedule_NoScheduleToday()` | Không có lịch | Không gửi lệnh |
| `testHandleSchedule_BeforeClassStartTime()` | Trước giờ lớp (chuẩn bị) | **Gửi lệnh điều chỉnh AC** |
| `testHandleSchedule_DuringClassTime()` | Trong giờ lớp | Không gửi lệnh |
| `testHandleSchedule_AfterClassTime()` | Sau lớp | **Gửi lệnh tắt** |

---

### 4. **Kiểm tra điều chỉnh nhiệt độ trước lớp (3 tests)**

| Test | Mô tả | Nhiệt độ | Kỳ vọng |
|------|-------|---------|--------|
| `testBuildPreClassState_LowTemperature()` | Nhiệt độ thấp | < 28°C | AC = OFF |
| `testBuildPreClassState_MediumTemperature()` | Nhiệt độ vừa | 28-32°C | AC = ECO |
| `testBuildPreClassState_HighTemperature()` | Nhiệt độ cao | > 32°C | AC = COOL |

---

### 5. **Kiểm tra tối ưu hóa (1 test + 1 không có lịch)**

| Test | Mô tả | Kỳ vọng |
|------|-------|--------|
| `testSendIfChanged_NoChange()` | State không thay đổi | Không gửi lệnh (tối ưu) |

---

## Cách chạy Tests

### **Option 1: Dùng IDE (IntelliJ IDEA / Eclipse)**
1. Mở file `AutoControlEngineServiceTest.java`
2. Click chuột phải → **Run 'AutoControlEngineServiceTest'**
3. Hoặc nhấn **Ctrl+Shift+F10** (Windows/Linux)

### **Option 2: Dùng Maven Command Line**
```bash
cd "path\to\Iot-Project"

# Chạy tất cả tests trong class
mvnw test -Dtest=AutoControlEngineServiceTest

# Chạy một test cụ thể
mvnw test -Dtest=AutoControlEngineServiceTest#testHandleEmptyRoom_RoomEmptyFor10Minutes

# Chạy tất cả tests với chi tiết
mvnw test -Dtest=AutoControlEngineServiceTest -X
```

### **Option 3: Dùng Gradle (nếu project dùng Gradle)**
```bash
./gradlew test --tests AutoControlEngineServiceTest
```

---

## Hiểu về Mocking trong Tests

File test sử dụng **Mockito** để mocking các dependencies:

```java
@Mock
private ClassroomRepository classroomRepository;

@Mock  
private SensorReadingRepository sensorReadingRepository;

@Mock
private MqttMessageHandlerService mqttMessageHandlerService;

@InjectMocks
private AutoControlEngineService autoControlEngineService;
```

### Cách mocking hoạt động:

```java
// Arrange: Setup mock behavior
when(classroomRepository.findById("classroom-001"))
    .thenReturn(Optional.of(testClassroom));

// Act: Gọi method cần test
autoControlEngineService.evaluate("classroom-001");

// Assert: Verify kết quả
verify(mqttMessageHandlerService, times(1))
    .sendControlCommand(anyString(), anyString(), anyString(), any());
```

---

## Kết quả mong đợi

Khi chạy tests, bạn sẽ thấy:
```
AutoControlEngineService Tests
  ✓ Test evaluate() - Classroom không tồn tại
  ✓ Test evaluate() - Device offline
  ✓ Test evaluate() - Device power OFF
  ✓ Test handleEmptyRoom() - Phòng rỗng >= 10 phút
  ✓ Test handleEmptyRoom() - Phòng rỗng < 10 phút
  ✓ Test handleEmptyRoom() - Không có dữ liệu sensor
  ✓ Test handleEmptyRoom() - Phòng vẫn có người
  ✓ Test handleSchedule() - Không có lịch
  ✓ Test handleSchedule() - Trước giờ lớp
  ✓ Test buildPreClassState() - Nhiệt độ < 28°C (AC OFF)
  ✓ Test buildPreClassState() - Nhiệt độ 28-32°C (AC ECO)
  ✓ Test buildPreClassState() - Nhiệt độ > 32°C (AC COOL)
  ✓ Test handleSchedule() - Trong giờ lớp
  ✓ Test handleSchedule() - Sau lớp
  ✓ Test sendIfChanged() - State không thay đổi

Tests run: 15, Failures: 0, Errors: 0
```

---

## Khắc phục lỗi thường gặp

### Lỗi 1: `java.lang.NullPointerException`
**Nguyên nhân**: Mock chưa được setup  
**Giải pháp**: Đảm bảo setup đúng trong `@BeforeEach` hoặc `when(...).thenReturn(...)`

### Lỗi 2: `Verification failed`
**Nguyên nhân**: Method không được gọi như mong đợi  
**Giải pháp**: Kiểm tra số lần gọi: `times(1)`, `never()`, `atLeastOnce()`

### Lỗi 3: `ClassNotFoundException`
**Nguyên nhân**: JUnit hoặc Mockito chưa được import  
**Giải pháp**: Thêm dependencies vào `pom.xml`:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Mở rộng Tests

Để thêm test case mới:

```java
@Test
@DisplayName("Test case mô tả")
void testNewFeature() throws JsonProcessingException {
    // Arrange: Chuẩn bị dữ liệu
    // ...
    
    // Act: Gọi method
    // ...
    
    // Assert: Kiểm tra kết quả
    // ...
}
```

---

**Tác giả**: GitHub Copilot  
**Ngày tạo**: 26/02/2026


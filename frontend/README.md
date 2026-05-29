# Smart Room HEMS IoT - Realtime Control Center

This is a premium, high-performance dark-mode React frontend for the **Smart Room IoT Management System**, built using **Vite, TypeScript, Tailwind CSS, Recharts, Zustand**, and **STOMP WebSockets**.

It connects directly with your **Spring Boot** server and is pre-configured to receive real-time sensor updates and device telemetry.

---

## 🚀 Getting Started

Follow these steps to run the frontend dashboard:

### 1. Prerequisite
Ensure you have **Node.js (v18 or higher)** installed on your machine.

### 2. Install Dependencies
Open your terminal inside the `frontend` folder and run:
```bash
npm install
```

### 3. Run Development Server
Boot the development server at `http://localhost:8081`:
```bash
npm run dev
```

---

## 🏗️ Architecture Design & Data Flow

This application is built with a **Stateless Layered Architecture**:

### 1. Authentication Flow (JWT RBAC)
*   **Decoupled Authentication:** The login screen (`pages/Login.tsx`) calls `/api/auth/login` to retrieve a JWT signed via **HS512** by the Spring Boot server.
*   **Store Layer (`store/authStore.ts`):** Decodes the JWT payload to extract active roles (e.g. `ROLE_ADMIN`, `USER`) and permissions.
*   **Security Shell (`App.tsx`):** Protects routes via `<ProtectedRoute>`. Admin panels (`/users` and `/roles`) are strictly guarded on the client side and matching backend `@PreAuthorize` guards.
*   **Axios Interceptor (`services/api.ts`):** Automatically injects `Authorization: Bearer <token>` on all requests.

### 2. Real-time Communication (WebSocket & STOMP)
*   **Active Subscriptions (`services/websocket.ts`):** Uses `@stomp/stompjs` over **SockJS** to connect to `/ws/smart-classroom`.
*   **Topic Synchronization:**
    *   `/topic/classroom/{id}/sensors` $\rightarrow$ Dynamic sensor updates (Temp, Humidity, PIR, Light Level).
    *   `/topic/classroom/{id}/state` $\rightarrow$ Online/Offline heartbeat and Master Power state.
    *   `/topic/classroom/{id}/control` $\rightarrow$ Hardware control feedback loop logs.
*   **Lifecycle Control:** Subscriptions are engaged *only* when viewing a specific room's cockpit details, and auto-disconnected on exit to conserve resources.

### 3. Optimistic UI Updates
*   When a user clicks an interactive control (e.g. turning on a Light Relay or toggling target temperature), the `classroomStore` instantly pushes an **Optimistic Preview** onto the active classroom state.
*   The UI updates within milliseconds.
*   If the backend/device fails to process (fails MQTT ACK), the store catches the error and reverts the UI state, guaranteeing zero lag in standard conditions.

---

## 📂 Project Structure Map

```
frontend/
├── package.json           # App dependencies (Recharts, Stomp, Zustand)
├── vite.config.ts         # Proxy maps & Path Aliases
├── tailwind.config.js     # Glassmorphic dark theme tokens
├── tsconfig.json          # Strict TypeScript rules
├── index.html             # Google fonts loader
└── src/
    ├── main.tsx           # Dom mounter
    ├── App.tsx            # Protected Router & Secure shell mounter
    ├── index.css          # Glow effects & Glassmorphic styles
    ├── types/
    │   └── index.ts       # Type definitions matching entity classes
    ├── services/
    │   ├── api.ts         # Axios client wrappers
    │   └── websocket.ts   # STOMP socket controller
    ├── store/
    │   ├── authStore.ts   # Zustand auth & JWT parser
    │   └── classroomStore.ts # Core IoT state synchronizer
    ├── components/
    │   ├── common/
    │   │   ├── Header.tsx # STOMP status & Profile
    │   │   └── Sidebar.tsx # Navigation & Role filter
    │   └── classroom/
    │       ├── ClassroomCard.tsx      # Classroom summary cards
    │       ├── DeviceControlPanel.tsx # Relays, AC modes, fans control
    │       ├── SensorDisplay.tsx      # Realtime environmental telemetry
    │       ├── LiveTelemetryChart.tsx # Scrolling trend lines
    │       └── ControlLogsTable.tsx   # Logs tables feed
    └── pages/
        ├── Login.tsx                  # Secure glowing auth portal
        ├── Dashboard.tsx              # Overview stats cockpit
        ├── ClassroomDetail.tsx        # Room detail control deck
        ├── UserManagement.tsx         # User credentials manager
        └── RolePermissionManagement.tsx # RBAC policy editor
```

---

## ⚡ Integration Details (Vite Proxy)
Vite is pre-configured in `vite.config.ts` to proxy requests automatically:
*   All `/api/**` calls map to `http://localhost:8080/api/**`
*   All `/ws/**` calls map to `ws://localhost:8080/ws/**`

This eliminates **CORS (Cross-Origin Resource Sharing)** issues during local development and connects to the backend automatically!

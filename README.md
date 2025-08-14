# Demo WebSocket Application

A comprehensive Spring Boot WebSocket application with real-time features including user management, exchange rates, and promotions. Built with Spring Boot 3.2.5, WebSocket + STOMP, Kafka, PostgreSQL, and JWT authentication.

## Features

- **User Management**: Registration, login, and profile management with JWT authentication
- **Real-time Exchange Rates**: Global exchange rates with real-time updates via WebSocket
- **User-specific Promotions**: Individual promotions with real-time updates
- **WebSocket + STOMP**: Real-time bidirectional communication
- **Kafka Integration**: Message streaming for real-time updates
- **PostgreSQL Database**: Persistent data storage with Flyway migrations
- **Swagger/OpenAPI**: Comprehensive API documentation
- **Exception Handling**: Centralized error handling with proper HTTP status codes
- **Security**: JWT-based authentication with Spring Security

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client App    │    │   WebSocket     │    │   Kafka         │
│   (Browser)     │◄──►│   + STOMP       │◄──►│   (Message      │
│                 │    │                 │    │   Broker)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Spring Boot   │
                       │   Application   │
                       │                 │
                       │  - Controllers  │
                       │  - Services     │
                       │  - Repositories │
                       └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   PostgreSQL    │
                       │   Database      │
                       └─────────────────┘
```

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Apache Kafka 2.8 or higher
- Apache Zookeeper (for Kafka)

## Setup Instructions

### 1. Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE demo_ws_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE demo_ws_db TO postgres;
```

2. The application will automatically create tables using Flyway migrations.

### 2. Kafka Setup

1. Download and extract Apache Kafka
2. Start Zookeeper:
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

3. Start Kafka:
```bash
bin/kafka-server-start.sh config/server.properties
```

### 3. Application Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd demo-ws
```

2. Update database configuration in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/demo_ws_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Build and run the application:
```bash
./gradlew build
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

### Exchange Rates
- `GET /api/exchange-rates` - Get all exchange rates
- `GET /api/exchange-rates/{fromCurrency}/{toCurrency}` - Get specific exchange rate
- `PUT /api/exchange-rates/{fromCurrency}/{toCurrency}` - Update exchange rate

### Promotions
- `GET /api/promotions` - Get user promotions
- `GET /api/promotions/all` - Get all active promotions
- `POST /api/promotions` - Create promotion
- `PUT /api/promotions/{id}` - Update promotion
- `DELETE /api/promotions/{id}` - Delete promotion

### WebSocket Endpoints
- `/ws` - WebSocket connection endpoint
- `/topic/exchange-rates` - Exchange rate updates (global)
- `/user/queue/promotions` - User-specific promotion updates

## WebSocket Usage

### Connection
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
});
```

### Subscribe to Exchange Rates
```javascript
stompClient.subscribe('/topic/exchange-rates', function (message) {
    const data = JSON.parse(message.body);
    console.log('Exchange rate update:', data);
});
```

### Subscribe to User Promotions
```javascript
stompClient.subscribe('/user/queue/promotions', function (message) {
    const data = JSON.parse(message.body);
    console.log('Promotion update:', data);
});
```

## Demo Application

Access the demo application at `http://localhost:8080` to test:

1. **User Registration/Login**: Create an account and authenticate
2. **WebSocket Connection**: Connect to real-time updates
3. **Exchange Rates**: View real-time exchange rate updates (simulated every 30 seconds)
4. **Promotions**: View user-specific promotions

## Real-time Features

### Exchange Rates
- Global updates every 30 seconds (simulated)
- All users see the same exchange rates
- Updates are broadcast to all connected clients

### Promotions
- User-specific promotions
- Real-time updates when promotions are created/updated
- Individual user queues for personalized updates

## Data Flow

1. **Exchange Rate Updates**:
   - Scheduled task simulates rate changes
   - Updates are saved to database
   - Published to Kafka topic
   - Broadcast to all WebSocket subscribers

2. **Promotion Updates**:
   - User creates/updates promotion
   - Saved to database
   - Published to Kafka topic
   - Sent to specific user's WebSocket queue

## Configuration

### Application Properties
- Database connection settings
- Kafka broker configuration
- JWT secret and expiration
- WebSocket allowed origins
- Swagger configuration

### Security
- JWT-based authentication
- Password encryption with BCrypt
- CORS configuration
- Role-based access control

## Monitoring

- **Health Check**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **API Documentation**: `http://localhost:8080/swagger-ui.html`

## Best Practices Implemented

1. **Layered Architecture**: Clear separation of concerns
2. **Exception Handling**: Centralized error handling
3. **Validation**: Input validation with proper error messages
4. **Security**: JWT authentication with proper token management
5. **Real-time Communication**: WebSocket + STOMP for bidirectional communication
6. **Message Brokering**: Kafka for reliable message delivery
7. **Database Management**: Flyway migrations for version control
8. **API Documentation**: Swagger/OpenAPI for comprehensive documentation
9. **Logging**: Proper logging with SLF4J
10. **Testing**: Unit tests for critical components

## Future Enhancements

1. **External Exchange Rate APIs**: Integrate with real exchange rate providers
2. **Redis Caching**: Add caching for frequently accessed data
3. **Microservices**: Split into separate microservices
4. **Docker Support**: Containerize the application
5. **Monitoring**: Add Prometheus metrics and Grafana dashboards
6. **Load Balancing**: Implement horizontal scaling
7. **Message Persistence**: Add message persistence for offline users

## Troubleshooting

### Common Issues

1. **Database Connection**: Ensure PostgreSQL is running and credentials are correct
2. **Kafka Connection**: Verify Kafka and Zookeeper are running
3. **WebSocket Connection**: Check CORS configuration for browser clients
4. **JWT Token**: Ensure proper token format in Authorization header

### Logs
Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

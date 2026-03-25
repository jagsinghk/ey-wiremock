# WireMock — JSONPlaceholder API Mock

A WireMock stub server that mirrors the [JSONPlaceholder](https://jsonplaceholder.typicode.com/) REST API for local testing and development.

---

## Project Structure

```
.
├── docker-compose.yml
├── mappings/
│   ├── posts/
│   │   ├── GET-posts.json              # GET /posts
│   │   ├── GET-posts-id.json           # GET /posts/{id}
│   │   ├── GET-posts-id-comments.json  # GET /posts/{id}/comments
│   │   ├── POST-posts.json             # POST /posts
│   │   ├── PUT-posts-id.json           # PUT /posts/{id}
│   │   ├── PATCH-posts-id.json         # PATCH /posts/{id}
│   │   └── DELETE-posts-id.json        # DELETE /posts/{id}
│   ├── comments/
│   │   ├── GET-comments.json           # GET /comments
│   │   ├── GET-comments-id.json        # GET /comments/{id}
│   │   └── GET-comments-by-postId.json # GET /comments?postId={id}
│   ├── users/
│   │   ├── GET-users.json              # GET /users
│   │   └── GET-users-id.json           # GET /users/{id}
│   ├── todos/
│   │   ├── GET-todos.json              # GET /todos
│   │   └── GET-todos-id.json           # GET /todos/{id}
│   ├── albums/
│   │   ├── GET-albums.json             # GET /albums
│   │   └── GET-albums-id.json          # GET /albums/{id}
│   └── photos/
│       ├── GET-photos.json             # GET /photos
│       └── GET-photos-id.json          # GET /photos/{id}
└── __files/
    ├── posts.json
    ├── comments.json
    ├── post-1-comments.json
    ├── users.json
    ├── todos.json
    ├── albums.json
    └── photos.json
```

---

## Running WireMock

### Option 1 — Docker (recommended)

```bash
docker compose up
```

WireMock starts on **http://localhost:8080**.

### Option 2 — WireMock Standalone JAR

Download the standalone JAR from https://wiremock.org/docs/download-and-installation/

```bash
java -jar wiremock-standalone-*.jar --port 8080 --verbose --global-response-templating
```

Run from the project root so WireMock picks up the `mappings/` and `__files/` directories automatically.

---

## Mocked Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/posts` | List all posts |
| GET | `/posts/{id}` | Get a single post |
| GET | `/posts/{id}/comments` | Get comments for a post |
| POST | `/posts` | Create a new post |
| PUT | `/posts/{id}` | Replace a post |
| PATCH | `/posts/{id}` | Partially update a post |
| DELETE | `/posts/{id}` | Delete a post |
| GET | `/comments` | List all comments |
| GET | `/comments/{id}` | Get a single comment |
| GET | `/comments?postId={id}` | Filter comments by post |
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get a single user |
| GET | `/todos` | List all todos |
| GET | `/todos/{id}` | Get a single todo |
| GET | `/albums` | List all albums |
| GET | `/albums/{id}` | Get a single album |
| GET | `/photos` | List all photos |
| GET | `/photos/{id}` | Get a single photo |

---

## Example Requests

```bash
# Get all posts
curl http://localhost:8080/posts

# Get a single post
curl http://localhost:8080/posts/1

# Get comments for a post (nested route)
curl http://localhost:8080/posts/1/comments

# Filter comments by postId (query param)
curl "http://localhost:8080/comments?postId=1"

# Create a post
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -d '{"title": "New Post", "body": "Some content", "userId": 1}'

# Update a post
curl -X PUT http://localhost:8080/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Updated Title", "body": "Updated body", "userId": 1}'

# Partially update a post
curl -X PATCH http://localhost:8080/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Patched Title"}'

# Delete a post
curl -X DELETE http://localhost:8080/posts/1

# Get a user
curl http://localhost:8080/users/1

# Get all todos
curl http://localhost:8080/todos
```

---

## WireMock Admin API

WireMock exposes an admin API at `/__admin/`:

```bash
# List all registered stubs
curl http://localhost:8080/__admin/mappings

# Reset all stubs to initial state
curl -X POST http://localhost:8080/__admin/reset

# Check which stubs were matched
curl http://localhost:8080/__admin/requests
```

---

## Notes

- **POST /posts** and **PUT /posts/{id}** use WireMock response templating to echo back the `title` and `body` fields from the request body.
- `--global-response-templating` enables Handlebars templating across all stubs.
- Response data is a representative subset of the real JSONPlaceholder dataset (not all 100 posts, 500 comments, etc.).
- To customise responses, edit the files in `__files/` (for lists) or the `jsonBody` field inside the relevant mapping file.

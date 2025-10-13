# AI Similarity Service

A small FastAPI service that computes semantic similarity between two pieces of text using the SentenceTransformers model `all-MiniLM-L6-v2`.

This repository contains a minimal API that accepts two texts and returns a cosine similarity score (float in the range [-1.0, 1.0]). The app is intentionally small so it can be used as a starting point for experimentation, integration, or testing.

## Contents

- `app/main.py` — FastAPI application and endpoint implementation.
- `requirements.txt` — Python dependencies used by the project.

## Quick contract (inputs / outputs)

- Endpoint: POST /similarity
- Input (JSON):
	- `text1`: string
	- `text2`: string
- Output (JSON):
	- `similarity`: float (cosine similarity; -1.0 to 1.0)

Error modes:
- 400/422 — invalid request body (missing or wrong types)
- 500 — server error (model load issues, OOM, etc.)

## Requirements

- Python 3.8+ (tested with 3.10+)
- Internet access the first time the model is downloaded (Hugging Face/model weights)
- Optional: a machine with more RAM/CPU for faster embeddings; GPU will speed up inference when configured

The exact packages are in `requirements.txt` — install into a virtual environment.

## Setup (recommended)

1. Create and activate a virtual environment:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

2. Install dependencies:

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

Note: The sentence-transformers package will automatically download the model weights the first time the app runs. That step requires internet access and may take a few minutes.

## Run locally

Start the server using Uvicorn (from the project root):

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

This will start the service at http://127.0.0.1:8000. FastAPI interactive docs are available at http://127.0.0.1:8000/docs.

## Example requests

Curl example:

```bash
curl -s -X POST "http://127.0.0.1:8000/similarity" \
	-H "Content-Type: application/json" \
	-d '{"text1":"I like apples", "text2":"I enjoy fruit"}'
```

Expected JSON response:

```json
{"similarity": 0.67}
```

Python example (requests):

```python
import requests

url = "http://127.0.0.1:8000/similarity"
payload = {"text1": "I like apples", "text2": "I enjoy fruit"}
resp = requests.post(url, json=payload)
print(resp.json())
```

## Notes & edge cases

- The returned similarity is a cosine similarity computed on model embeddings and should be interpreted as a relative measure. Thresholds for "similar" vs "not similar" depend on your use case.
- Very short or empty strings can produce noisy results. Consider validating input length in a production service.
- Model downloads require disk space (~100–500MB depending on model) and time on first-run.
- On memory-constrained machines, ensure enough RAM is available — sentence-transformers may allocate intermediate tensors.

## Troubleshooting

- If the app fails to start with an OOM (out-of-memory) error, try running on a machine with more RAM or use a smaller model.
- If the model download fails, check internet access, firewall/proxy settings, or consider pre-downloading the model in a separate step.

## Next steps / suggestions

- Add input validation (max length) and clearer error messages.
- Add integration tests for the endpoint (a few happy-path and error-path tests).
- Add Dockerfile for consistent deployment.

## License

This repository contains example code; add a license file if you plan to redistribute or publish.

---

If you want, I can also:
- Add a small test that exercises `POST /similarity`.
- Add a Dockerfile and an updated `requirements.txt` tuned for smaller installs.
Just tell me which you'd like next.


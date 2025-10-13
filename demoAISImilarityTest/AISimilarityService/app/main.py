# Import necessary libraries
from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util

# initialize FastAPI app and model
app = FastAPI(title="AI Similarity Service")
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

# BaseModel for input data
class TextPair(BaseModel):
    text1: str
    text2: str

# API endpoint to calculate similarity
@app.post("/similarity")
async def get_similarity(data: TextPair):
    emb1 = model.encode(data.text1, convert_to_tensor=True)
    emb2 = model.encode(data.text2, convert_to_tensor=True)
    score = util.cos_sim(emb1, emb2)
    return {"similarity": float(score.item())}

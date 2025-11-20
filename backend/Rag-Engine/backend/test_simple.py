#!/usr/bin/env python3
"""
Simple test script to verify the RAG system components
"""

import sys
import os

# Add parent directory to path
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

def test_imports():
    """Test if all required modules can be imported"""
    print("🔍 Testing imports...")
    
    try:
        from query_to_embedding import query_to_embedding
        print("✅ query_to_embedding imported successfully")
    except Exception as e:
        print(f"❌ query_to_embedding import failed: {e}")
        return False
    
    try:
        from opensearch_query_processor import OpenSearchQueryProcessor
        print("✅ OpenSearchQueryProcessor imported successfully")
    except Exception as e:
        print(f"❌ OpenSearchQueryProcessor import failed: {e}")
        return False
    
    try:
        import google.generativeai as genai
        print("✅ google.generativeai imported successfully")
    except Exception as e:
        print(f"❌ google.generativeai import failed: {e}")
        return False
    
    return True

def test_embedding():
    """Test embedding generation"""
    print("\n🧠 Testing embedding generation...")
    
    try:
        from query_to_embedding import query_to_embedding
        test_query = "What are the safety requirements?"
        embedding = query_to_embedding(test_query)
        print(f"✅ Embedding generated successfully")
        print(f"   Query: '{test_query}'")
        print(f"   Embedding length: {len(embedding)}")
        return True
    except Exception as e:
        print(f"❌ Embedding generation failed: {e}")
        return False

def test_opensearch():
    """Test OpenSearch connection"""
    print("\n🔌 Testing OpenSearch connection...")
    
    try:
        from opensearch_query_processor import OpenSearchQueryProcessor
        processor = OpenSearchQueryProcessor()
        print("✅ OpenSearch connection successful")
        
        # Try to get stats
        try:
            stats = processor.get_index_stats()
            print(f"   Documents: {stats.get('total_documents', 'Unknown')}")
        except Exception as e:
            print(f"   ⚠️  Index stats failed: {e}")
            print("   This is normal if no embeddings are uploaded yet")
        
        return True
    except Exception as e:
        print(f"❌ OpenSearch connection failed: {e}")
        return False

def test_gemini():
    """Test Gemini API connection"""
    print("\n🤖 Testing Gemini API...")
    
    try:
        import google.generativeai as genai
        genai.configure(api_key="AIzaSyCn10Dq_CBqwllD3R3Qt8oh2VLIZkrpbCY")
        model = genai.GenerativeModel('gemini-2.0-flash-exp')
        
        # Simple test
        response = model.generate_content("Hello, are you working?")
        print("✅ Gemini API connection successful")
        print(f"   Response: {response.text[:100]}...")
        return True
    except Exception as e:
        print(f"❌ Gemini API test failed: {e}")
        return False

def main():
    """Run all tests"""
    print("🚀 KMRM RAG System - Component Tests")
    print("=" * 50)
    
    tests = [
        ("Imports", test_imports),
        ("Embedding Generation", test_embedding),
        ("OpenSearch Connection", test_opensearch),
        ("Gemini API", test_gemini)
    ]
    
    results = []
    for test_name, test_func in tests:
        try:
            result = test_func()
            results.append((test_name, result))
        except Exception as e:
            print(f"❌ {test_name} test crashed: {e}")
            results.append((test_name, False))
    
    print("\n" + "=" * 50)
    print("📊 Test Results:")
    
    all_passed = True
    for test_name, result in results:
        status = "✅ PASS" if result else "❌ FAIL"
        print(f"   {test_name}: {status}")
        if not result:
            all_passed = False
    
    if all_passed:
        print("\n🎉 All tests passed! Your RAG system is ready.")
        print("\nNext steps:")
        print("1. Upload embeddings to OpenSearch (if not done)")
        print("2. Start the server: python3 app.py")
        print("3. Open browser: http://localhost:5001")
    else:
        print("\n⚠️  Some tests failed. Please check the errors above.")
        print("\nTroubleshooting:")
        print("1. Install missing dependencies: pip install -r requirements.txt")
        print("2. Start OpenSearch: docker-compose up -d")
        print("3. Check your Gemini API key")

if __name__ == "__main__":
    main()

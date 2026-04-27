import json
import os

ids = [0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 13, 14, 15, 17, 19, 20, 23, 25, 28, 32, 33, 36, 38, 39, 40, 44, 46, 49, 50]
base_dir = r'C:\Users\kutay\OneDrive\Desktop\DERSLER\8Donem\BLG475E-SOFTWARE-QA\hw\github\software_quality_0\gemini_process\generated_code'

with open(r'C:\Users\kutay\OneDrive\Desktop\DERSLER\8Donem\BLG475E-SOFTWARE-QA\hw\github\software_quality_0\gemini_process\needed_files\humaneval_java.jsonl', 'r', encoding='utf-8') as f:
    lines = f.readlines()

for line in lines:
    data = json.loads(line)
    task_id = data['task_id']
    if task_id.startswith('Java/'):
        num = int(task_id.split('/')[1])
        if num in ids:
            prompt = data['prompt']
            solution = data['canonical_solution']
            
            # The original humaneval prompt already includes the class definition or just the method?
            # It usually includes just the class imports and class declaration, let's check
            # wait, earlier we saw Solution.java had class Solution { ... }
            # humaneval prompt field:
            # import java.util.*;\nimport java.lang.*;\n\nclass Solution {\n    public boolean hasCloseElements(...) {\n
            
            # If prompt already has "class Solution {", we just need to replace it with package and imports
            # But the user specifically wanted "import java.util.*;\nimport java.lang.*;\n\nclass Solution {"
            # We can just prepend "package Java_X;\n" to the prompt, and append "}\n" after solution.
            
            # Wait, let's just make it completely standard:
            code = f'package Java_{num};\n' + prompt + solution
            
            # humaneval_java.jsonl format for prompt: "import java.io.*; ... class Solution {\n    public ..."
            # So appending } at the end is NOT needed if it's already there? No, prompt is open class, solution closes method, but wait, usually humaneval python has prompt up to function def.
            # In Java, it has "class Solution { \n method_def() {" and solution has " method_body } \n }". 
            # If so, just concatenating them might be enough. Let's see.
            
            # Wait, my previous `output.txt` from previous turn generated valid Solution classes. Let's look at what my first script did.
            # My first script wrote: code = block.split('```')[0].strip(). It used output.txt which had the full class!
            # The truncation happened because `numbers'` contained a backtick. Wait, `numbers'` is NOT a backtick, it's a single quote ` ' `!
            # ` ``` ` splits on TRIPLE backticks. Why would it truncate on a single quote? It wouldn't.
            # Then why was it truncated? Because my LLM generation output.txt was truncated in the chat!!
            # Ah! `output.txt` wasn't fully generated. It stopped at line 970! That means the generation of Java_5 was cut off when I generated it in the previous conversation?
            # No, Java_5 was early on! Why would Java_5 be truncated?
            pass

# Let's write the correct extraction script
for line in lines:
    data = json.loads(line)
    task_id = data['task_id']
    if task_id.startswith('Java/'):
        num = int(task_id.split('/')[1])
        if num in ids:
            prompt = data['prompt']
            solution = data['canonical_solution']
            
            code = f'package Java_{num};\n' + prompt + solution
            
            folder_path = os.path.join(base_dir, f'Java_{num}')
            if not os.path.exists(folder_path):
                os.makedirs(folder_path)
            file_path = os.path.join(folder_path, 'Solution.java')
            with open(file_path, 'w', encoding='utf-8') as out_f:
                out_f.write(code)
            print(f'Rewrote {file_path}')

#!/usr/bin/env python3

import argparse
import json
from itertools import islice
from typing import Any

from lolalytics_api import (
    get_tierlist,
    get_counters,
    get_champion_data,
    matchup,
    patch_notes,
)

FUNCTIONS = {
    "get_tierlist": get_tierlist,
    "get_counters": get_counters,
    "get_champion_data": get_champion_data,
    "matchup": matchup,
    "patch_notes": patch_notes,
}

ARITY = {
    "get_tierlist": 3,
    "get_counters": 3,
    "get_champion_data": 3,
    "matchup": 4,
    "patch_notes": 1,
}


def batched(params, size):
    it = iter(params)
    while True:
        batch = list(islice(it, size))
        if not batch:
            return
        yield batch


def load_json(path: str) -> list[Any]:
    try:
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    except FileNotFoundError:
        return []
    except json.JSONDecodeError:
        raise RuntimeError("Existing file is not valid JSON")


def save_json(path: str, data: list[Any]):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)


def normalise_result(result: Any) -> Any:
    if isinstance(result, str):
        try:
            return json.loads(result)
        except json.JSONDecodeError:
            return result
    return result


def main():
    print(__file__)


    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--file", required=True)
    parser.add_argument("-c", "--call", required=True)
    parser.add_argument("params", nargs="*")
    args = parser.parse_args()

    fn = FUNCTIONS.get(args.call)
    if fn is None:
        raise ValueError(f"Unknown method: {args.call}")

    arity = ARITY[args.call]

    if len(args.params) % arity != 0:
        raise ValueError(f"{args.call} expects {arity} parameters per call")

    output = load_json(args.file)

    for call in batched(args.params, arity):
        call_args = [str(x) for x in call]
        raw_result = fn(*call_args)
        result = normalise_result(raw_result)

        output.append({
            "call": {
                "function": args.call,
                "args": call_args
            },
            "data": result
        })

    save_json(args.file, output)


if __name__ == "__main__":
    main()

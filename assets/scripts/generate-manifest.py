# %% Imports
import csv
import random
import datetime


# %% Helper
def random_function(mean, sigma):
    return lambda: max(int(random.gauss(mean, sigma)), 1)


# %% Consts
station_names = [
    "Kiyevskaya",
    "Borovitskaya",
    "Kropotkinskaya",
    "Novokuznetskaya",
    "Taganskaya",
    "Avtozavodskaya",
    "Nagatinskaya",
    "Tsaritsino",
    "Yujnaya",
    "Lesoparkovaya",
    "Bitsevsky",
    "Bulva Dimitriya Doonskogo",
    "Ulitsa Skobelevskaya",
    "Lesoparkovaya",
    "Bulvar Admirala Ushkova",
    "Ulitsa Gorchakova",
    "Annino",
    "Ulitsa Starokachalovskaya",
]

items_probabilities = {
    "Concrete (Tonnes)": 0.75,
    "Rebar (Meters)": 0.6,
    "High Density Insulation (1m²)": 0.35,
    "2mm Insulated Copper Wiring (Meters)": 0.20,
    "Refrigerant (50 Liter Drum)": 0.05,
    "AK 74": 0.05,
    "Makarov": 0.05,
    "Copper Piping (Meters)": 0.1,
    "Series 1 Computer Workstation": 0.1,
    "Series 2 Computer Workstation": 0.1,
    "Wood Dimension 2x4 (Pallets)": 0.25,
    "Steel Rail (Meters)": 0.25,
}

items_amounts = {
    "Concrete (Tonnes)": random_function(1000, 500),
    "Rebar (Meters)": random_function(800, 200),
    "High Density Insulation (1m²)": random_function(500, 400),
    "2mm Insulated Copper Wiring (Meters)": random_function(1500, 500),
    "Refrigerant (50 Liter Drum)": random_function(10, 30),
    "AK 74": random_function(10, 10),
    "Makarov": random_function(10, 10),
    "Copper Piping (Meters)": random_function(500, 250),
    "Series 1 Computer Workstation": random_function(10, 5),
    "Series 2 Computer Workstation": random_function(10, 5),
    "Wood Dimension 2x4 (Pallets)": random_function(100, 25),
    "Steel Rail (Meters)": random_function(100, 25),
}

statuses = {
    "Denied": 0.05,
    "Pending Review": 0.05,
    "Completed": 0.4,
    "Delayed": 0.15,
}


def random_item(d):
    population = list(d.keys())
    weights = list(d.values())
    return random.choices(population, weights)[0]


def random_date():
    start = datetime.date(1990, 1, 1)
    end = datetime.date(2000, 1, 1)
    delta = end - start
    days_between = delta.days
    random_num_days = random.randrange(days_between)
    random_date = start + datetime.timedelta(days=random_num_days)
    return random_date.strftime("%m/%d/%Y")


def generate_row():
    location = random.choice(station_names)
    key = random_item(items_probabilities)
    amount = items_amounts[key]()
    amount = round(amount, -1) if amount > 10 else amount

    status = random_item(statuses)
    date_requested = random_date()
    d = {
        "Metro Station": location,
        "Item": key,
        "Status": status,
        "Amount": amount,
        "Date Requested": date_requested,
    }
    return d


# %% Controls
N = 80
# %%
with open("manifest.csv", mode="w") as fout:
    writer = csv.DictWriter(
        fout, fieldnames=["Metro Station", "Item", "Status", "Amount", "Date Requested"]
    )

    writer.writeheader()
    for _ in range(N):
        writer.writerow(generate_row())

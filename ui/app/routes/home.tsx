import type { Route } from "./+types/home";
import { DataTable } from "./data-table";
import { columns } from "./columns";
import type { User } from "~/types/user";

function getApiOrigin() {
  if (import.meta.env.DEV) {
    return "http://127.0.0.1:8080";
  }

  return window.location.origin;
}

export async function clientLoader({}: Route.ClientLoaderArgs) {
  const apiOrigin = getApiOrigin();
  const response = await fetch(new URL("/api/users", apiOrigin), {
    credentials: "include",
  });

  if (response.status === 401) {
    window.location.assign(
      new URL("/oauth2/authorization/github", apiOrigin).toString(),
    );
    throw new Response(null, {
      status: 401,
      statusText: "Redirecting to GitHub login",
    });
  }

  if (!response.ok) {
    throw new Response(null, {
      status: response.status,
      statusText: response.statusText,
    });
  }

  const users: User[] = await response.json();
  return { users };
}

export function HydrateFallback() {
  return <div className="container mx-auto py-10">Loading users...</div>;
}

export function meta({}: Route.MetaArgs) {
  return [
    { title: "New React Router App" },
    { name: "description", content: "Welcome to React Router!" },
  ];
}

export default function Home({ loaderData }: Route.ComponentProps) {
  const { users } = loaderData;

  return (
    <div className="container mx-auto py-10">
      <DataTable columns={columns} data={users} />
    </div>
  );
}
